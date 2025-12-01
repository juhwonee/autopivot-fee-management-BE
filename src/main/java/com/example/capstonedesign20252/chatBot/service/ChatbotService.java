package com.example.capstonedesign20252.chatBot.service;

import com.example.capstonedesign20252.chatBot.dto.ChatResponseDto;
import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.group.service.GroupService;
import com.example.capstonedesign20252.groupMember.domain.GroupMember;
import com.example.capstonedesign20252.groupMember.repository.GroupMemberRepository;
import com.example.capstonedesign20252.payment.domain.Payment;
import com.example.capstonedesign20252.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

  private final GeminiService geminiService;
  private final PaymentRepository paymentRepository;
  private final GroupMemberRepository groupMemberRepository;
  private final GroupService groupService;

  private static final String SYSTEM_PROMPT = """
      당신은 '오토피봇(Auto Fee Bot)' 동아리 회비 관리 시스템의 AI 도우미 두레입니다.
      
      주요 기능:
      1. 회비 납부 현황 조회
      2. 미납자 명단 확인
      3. 회비 통계 정보 제공
      4. 엑셀 보고서 생성 안내
      
      답변 규칙:
      - 친근하고 명확하게 답변하세요
      - 한국어로 답변하세요
      - 모르는 내용은 솔직히 모른다고 하세요
      - 불필요하게 길지 않게 답변하세요
      - 질문자는 40~50대 대상이므로 간단하고 명료하게 답변하세요
      - 볼드체를 강조한다고 **문장** 형식으로 답변하지 말고 [문장] 와 같은 형식으로 강조할 것.
      """;

  /**
   * 메시지 처리 메인 메서드
   */
  public ChatResponseDto processMessage(Long groupId, String userMessage) {
    Group group = groupService.findByGroupId(groupId);

    log.info("{}번 그룹 {}에서 대화를 시작합니다. 대화 내용: {}",
        group.getId(), group.getGroupName(), userMessage);

    try {
      // 빠른 응답 처리 (키워드 기반)
      ChatResponseDto quickResponse = handleQuickResponse(groupId, userMessage);
      if (quickResponse != null) {
        return quickResponse;
      }

      // AI 응답 (Gemini)
      String aiResponse = geminiService.chat(SYSTEM_PROMPT, userMessage);
      return new ChatResponseDto(aiResponse, "text", null);

    } catch (Exception e) {
      log.error("챗봇 처리 중 오류 발생", e);
      return new ChatResponseDto(
          "죄송합니다. 일시적인 오류가 발생했습니다. 다시 시도해주세요.",
          "text",
          null
      );
    }
  }

  /**
   * 키워드 기반 빠른 응답 처리
   */
  private ChatResponseDto handleQuickResponse(Long groupId, String message) {
    if (message == null) return null;

    String lowerMessage = message.toLowerCase().trim();

    // 미납자 관련 키워드
    if (lowerMessage.contains("미납") || lowerMessage.contains("안 낸") ||
        lowerMessage.contains("안낸") || lowerMessage.contains("연체")) {
      return getUnpaidMembers(groupId);
    }

    // 현황/통계 관련 키워드
    if (lowerMessage.contains("현황") || lowerMessage.contains("통계") ||
        lowerMessage.contains("회비") || lowerMessage.contains("납부율")) {
      return getPaymentStatistics(groupId);
    }

    // 납부 완료자 관련 키워드
    if (lowerMessage.contains("납부") && (lowerMessage.contains("완료") || lowerMessage.contains("한 사람"))) {
      return getPaidMembers(groupId);
    }

    // 도움말
    if (lowerMessage.contains("도움") || lowerMessage.contains("help") ||
        lowerMessage.contains("사용법") || lowerMessage.contains("안내")) {
      return getHelpMessage();
    }

    return null;
  }

  /**
   * ✅ 미납자 조회 - FeeService와 동일한 로직
   * 현재 월(period) 기준으로 PENDING 또는 OVERDUE 상태인 멤버 조회
   */
  private ChatResponseDto getUnpaidMembers(Long groupId) {
    Group group = groupService.findByGroupId(groupId);

    // ✅ 실제 그룹 멤버 목록 조회
    List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);

    if (members.isEmpty()) {
      return new ChatResponseDto("등록된 회원이 없습니다.", "text", null);
    }

    // ✅ 현재 월 기준 Payment 조회 (FeeService와 동일)
    String currentPeriod = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    List<Payment> payments = paymentRepository.findByGroupIdAndPaymentPeriod(groupId, currentPeriod);

    // ✅ memberId -> Payment 매핑 (FeeService와 동일)
    Map<Long, Payment> paymentMap = payments.stream()
                                            .collect(Collectors.toMap(
                                                p -> p.getGroupMember().getId(),
                                                p -> p,
                                                (p1, p2) -> p1
                                            ));

    LocalDateTime now = LocalDateTime.now();

    // ✅ 미납자 분류 (PENDING, OVERDUE 분리)
    List<GroupMember> pendingMembers = new ArrayList<>();  // 납부 대기
    List<GroupMember> overdueMembers = new ArrayList<>();  // 연체

    for (GroupMember member : members) {
      Payment payment = paymentMap.get(member.getId());

      if (payment == null) {
        // Payment 레코드가 없음 = 아직 납부 안 함 (PENDING)
        pendingMembers.add(member);
      } else {
        String status = payment.getStatus();

        // PENDING인데 마감일 지났으면 OVERDUE
        if ("PENDING".equals(status) && payment.getDueDate() != null
            && payment.getDueDate().isBefore(now)) {
          status = "OVERDUE";
        }

        if ("PENDING".equals(status)) {
          pendingMembers.add(member);
        } else if ("OVERDUE".equals(status)) {
          overdueMembers.add(member);
        }
        // PAID는 제외
      }
    }

    // 미납자가 없는 경우
    if (pendingMembers.isEmpty() && overdueMembers.isEmpty()) {
      return new ChatResponseDto(
          String.format("🎉 [%s] 모든 회원이 회비를 납부 완료했습니다!", currentPeriod),
          "text",
          null
      );
    }

    // 응답 메시지 생성
    StringBuilder response = new StringBuilder();
    response.append(String.format("[%s 미납자 명단]\n\n", currentPeriod));

    // 연체자 먼저 표시 (더 중요)
    if (!overdueMembers.isEmpty()) {
      response.append("🔴 연체 (마감일 초과)\n");
      for (GroupMember member : overdueMembers) {
        response.append(String.format("  - %s (%s)\n", member.getName(), formatPhone(member.getPhone())));
      }
      response.append("\n");
    }

    // 납부 대기자
    if (!pendingMembers.isEmpty()) {
      response.append("🟡 납부 대기\n");
      for (GroupMember member : pendingMembers) {
        response.append(String.format("  - %s (%s)\n", member.getName(), formatPhone(member.getPhone())));
      }
    }

    response.append(String.format("\n총 %d명 미납", pendingMembers.size() + overdueMembers.size()));

    // 데이터도 함께 반환
    List<GroupMember> allUnpaid = new ArrayList<>();
    allUnpaid.addAll(overdueMembers);
    allUnpaid.addAll(pendingMembers);

    return new ChatResponseDto(response.toString(), "list", Map.of(
        "period", currentPeriod,
        "overdueMembers", overdueMembers,
        "pendingMembers", pendingMembers,
        "totalUnpaid", allUnpaid.size()
    ));
  }

  /**
   * ✅ 납부 완료자 조회
   */
  private ChatResponseDto getPaidMembers(Long groupId) {
    Group group = groupService.findByGroupId(groupId);
    List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);

    if (members.isEmpty()) {
      return new ChatResponseDto("등록된 회원이 없습니다.", "text", null);
    }

    String currentPeriod = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    List<Payment> payments = paymentRepository.findByGroupIdAndPaymentPeriod(groupId, currentPeriod);

    // PAID 상태인 Payment의 멤버 정보 추출
    List<Map<String, Object>> paidList = payments.stream()
                                                 .filter(p -> "PAID".equals(p.getStatus()))
                                                 .map(p -> Map.<String, Object>of(
                                                     "name", p.getGroupMember().getName(),
                                                     "phone", p.getGroupMember().getPhone(),
                                                     "amount", p.getAmount(),
                                                     "paidAt", p.getPaidAt() != null ? p.getPaidAt() : LocalDateTime.now()
                                                 ))
                                                 .collect(Collectors.toList());

    if (paidList.isEmpty()) {
      return new ChatResponseDto(
          String.format("[%s] 아직 납부 완료한 회원이 없습니다.", currentPeriod),
          "text",
          null
      );
    }

    StringBuilder response = new StringBuilder();
    response.append(String.format("[%s 납부 완료자]\n\n", currentPeriod));

    for (Map<String, Object> paid : paidList) {
      Object paidAtObj = paid.get("paidAt");
      String paidDate = "";
      if (paidAtObj instanceof LocalDateTime) {
        paidDate = ((LocalDateTime) paidAtObj).format(DateTimeFormatter.ofPattern("M/d HH:mm"));
      }
      response.append(String.format("✅ %s - %s\n", paid.get("name"), paidDate));
    }

    response.append(String.format("\n총 %d명 납부 완료", paidList.size()));

    return new ChatResponseDto(response.toString(), "list", Map.of(
        "period", currentPeriod,
        "paidMembers", paidList,
        "totalPaid", paidList.size()
    ));
  }

  /**
   * ✅ 회비 통계 - FeeService/DashboardService와 완전히 동일한 로직
   */
  private ChatResponseDto getPaymentStatistics(Long groupId) {
    Group group = groupService.findByGroupId(groupId);

    // ✅ 핵심: 실제 그룹 멤버 목록 조회 (Payment 개수 X)
    List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);

    if (members.isEmpty()) {
      return new ChatResponseDto(
          "아직 등록된 회원이 없습니다.",
          "text",
          null
      );
    }

    // ✅ 현재 월 기준 Payment만 조회
    String currentPeriod = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    List<Payment> payments = paymentRepository.findByGroupIdAndPaymentPeriod(groupId, currentPeriod);

    // ✅ memberId -> Payment 매핑
    Map<Long, Payment> paymentMap = payments.stream()
                                            .collect(Collectors.toMap(
                                                p -> p.getGroupMember().getId(),
                                                p -> p,
                                                (p1, p2) -> p1
                                            ));

    LocalDateTime now = LocalDateTime.now();

    // ✅ 멤버별 상태 계산 (FeeService와 동일)
    int paidCount = 0;
    int pendingCount = 0;
    int overdueCount = 0;
    long totalCollected = 0;

    for (GroupMember member : members) {
      Payment payment = paymentMap.get(member.getId());

      if (payment == null) {
        // Payment 레코드가 없으면 PENDING
        pendingCount++;
      } else {
        String status = payment.getStatus();

        // PENDING이면서 마감일이 지났으면 OVERDUE
        if ("PENDING".equals(status) && payment.getDueDate() != null
            && payment.getDueDate().isBefore(now)) {
          status = "OVERDUE";
        }

        switch (status) {
          case "PAID":
            paidCount++;
            totalCollected += payment.getAmount().longValue();
            break;
          case "OVERDUE":
            overdueCount++;
            break;
          default:  // PENDING
            pendingCount++;
            break;
        }
      }
    }

    // ✅ 핵심: totalMembers = 실제 멤버 수 (Payment 개수 X)
    int totalMembers = members.size();
    int unpaidMembers = totalMembers - paidCount;

    // ✅ 목표 금액 = 회비 × 실제 멤버 수
    long targetAmount = (long) group.getFee() * totalMembers;

    // 미수금 = 목표 금액 - 수금액
    long remainingAmount = targetAmount - totalCollected;

    // ✅ 납부율 = (납부 인원 / 총 인원) × 100
    int paymentRate = totalMembers == 0 ? 0 : (paidCount * 100) / totalMembers;

    log.info("통계 계산 완료 - period: {}, 멤버: {}명, 납부: {}명, 미납: {}명, 연체: {}명, 납부율: {}%",
        currentPeriod, totalMembers, paidCount, pendingCount, overdueCount, paymentRate);

    // 응답 메시지 생성
    String response = String.format("""
            [%s 회비 현황]
            
            👥 전체 회원: %d명
            ✅ 납부 완료: %d명
            🟡 납부 대기: %d명
            🔴 연체: %d명
            
            💰 수금 금액: %,d원
            🎯 목표 금액: %,d원
            📊 미수금: %,d원
            
            납부율: %d%%
            """,
        currentPeriod,
        totalMembers,
        paidCount,
        pendingCount,
        overdueCount,
        totalCollected,
        targetAmount,
        remainingAmount,
        paymentRate
    );

    // 통계 데이터 맵 생성 (Map.of()는 10개 제한이 있어서 HashMap 사용)
    Map<String, Object> statisticsData = new HashMap<>();
    statisticsData.put("period", currentPeriod);
    statisticsData.put("groupName", group.getGroupName());
    statisticsData.put("monthlyFee", group.getFee());
    statisticsData.put("totalMembers", totalMembers);
    statisticsData.put("paidCount", paidCount);
    statisticsData.put("pendingCount", pendingCount);
    statisticsData.put("overdueCount", overdueCount);
    statisticsData.put("unpaidMembers", unpaidMembers);
    statisticsData.put("totalCollected", totalCollected);
    statisticsData.put("targetAmount", targetAmount);
    statisticsData.put("remainingAmount", remainingAmount);
    statisticsData.put("paymentRate", paymentRate);

    return new ChatResponseDto(response, "statistics", statisticsData);
  }

  /**
   * 도움말 메시지
   */
  private ChatResponseDto getHelpMessage() {
    String helpText = """
        🤖 [오토피봇 사용 가이드]
        
        💡 "미납자 알려줘" - 미납/연체 회원 명단
        💡 "회비 현황" - 이번 달 납부 통계
        💡 "납부 완료한 사람" - 납부 완료자 명단
        💡 "납부율이 어떻게 돼?" - 현재 납부율
        
        궁금한 점이 있으면 자유롭게 물어보세요! 😊
        """;

    return new ChatResponseDto(helpText, "text", null);
  }

  /**
   * 전화번호 포맷팅 (프라이버시 보호)
   */
  private String formatPhone(String phone) {
    if (phone == null || phone.length() < 4) {
      return "***";
    }
    // 뒤 4자리만 표시
    return "***-" + phone.substring(phone.length() - 4);
  }
}
