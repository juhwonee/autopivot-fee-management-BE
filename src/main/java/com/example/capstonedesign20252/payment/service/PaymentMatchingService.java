package com.example.capstonedesign20252.payment.service;

import com.example.capstonedesign20252.dashboard.service.DashboardService;
import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.group.repository.GroupRepository;
import com.example.capstonedesign20252.payment.domain.Payment;
import com.example.capstonedesign20252.payment.domain.PaymentLog;
import com.example.capstonedesign20252.payment.repository.PaymentRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentMatchingService {

  private final GroupRepository groupRepository;
  private final PaymentRepository paymentRepository;
  private final DashboardService dashboardService;

  /**
   * PaymentLog와 Payment 매칭 및 처리
   *
   * 매칭 로직:
   * 1. 통장 이름으로 Group 찾기
   * 2. 입금자명 + 금액으로 Payment 매칭
   * 3. 매칭 성공 시 상태 업데이트 + 캐시 삭제
   */
  @Transactional
  public boolean matchAndProcessPayment(PaymentLog paymentLog) {
    log.info("🔍 입금 매칭 시작 - 입금자: {}, 금액: {}원, 통장: {}",
        paymentLog.getName(),
        paymentLog.getAmount(),
        paymentLog.getTargetAccount());

    try {
      Optional<Group> groupOpt = findGroupByAccountName(paymentLog.getTargetAccount());

      if (groupOpt.isEmpty()) {
        log.warn("⚠️ 통장 이름과 매칭되는 그룹 없음: {}", paymentLog.getTargetAccount());
        return false;
      }

      Group group = groupOpt.get();
      log.info("✅ 그룹 매칭 성공: {} (ID: {})", group.getGroupName(), group.getId());

      List<Payment> pendingPayments = paymentRepository.findPendingPaymentsByGroup(group.getId());

      if (pendingPayments.isEmpty()) {
        log.warn("⚠️ 그룹에 PENDING 상태의 결제 건 없음: {}", group.getGroupName());
        return false;
      }

      BigDecimal amount = BigDecimal.valueOf(paymentLog.getAmount());
      Optional<Payment> matchedPayment = pendingPayments.stream()
                                                        .filter(p -> matchesPayment(p, paymentLog.getName(), amount))
                                                        .findFirst();

      if (matchedPayment.isEmpty()) {
        log.warn("⚠️ 매칭되는 결제 건 없음 - 입금자: {}, 금액: {}원",
            paymentLog.getName(), amount);
        return false;
      }

      Payment payment = matchedPayment.get();
      payment.markAsPaid(paymentLog.getReceivedAt());
      paymentRepository.save(payment);
      paymentLog.markAsProcessed(payment.getId());
      dashboardService.evictDashboardCache(group.getId());

      log.info("🎉 입금 매칭 성공! - PaymentLog ID: {}, Payment ID: {}, 회원: {}",
          paymentLog.getId(), payment.getId(), payment.getGroup().getUser().getName());

      return true;

    } catch (Exception e) {
      log.error("❌ 입금 매칭 중 오류 발생", e);
      return false;
    }
  }

  /**
   * 통장 이름으로 그룹 찾기
   *
   * 우선순위:
   * 1. 정확히 일치 (예: "ICON 모임 통장" = "ICON 모임 통장")
   * 2. 통장 이름에 그룹명 포함 (예: "ICON 모임 통장"에 "ICON" 포함)
   */
  private Optional<Group> findGroupByAccountName(String accountName) {
    Optional<Group> exactMatch = groupRepository.findByAccountName(accountName);
    if (exactMatch.isPresent()) {
      return exactMatch;
    }

    List<Group> partialMatches = groupRepository.findByAccountNameOrContainsGroupName(accountName);
    if (!partialMatches.isEmpty()) {
      if (partialMatches.size() > 1) {
        log.warn("⚠️ 통장 이름과 여러 그룹이 매칭됨: {} → 첫 번째 선택", accountName);
      }
      return Optional.of(partialMatches.get(0));
    }

    return Optional.empty();
  }

  /**
   * Payment와 입금 정보가 매칭되는지 확인
   *
   * 매칭 조건:
   * 1. 금액이 정확히 일치
   * 2. 입금자명이 회원명과 일치 (정확히 or 정규화 후)
   */
  private boolean matchesPayment(Payment payment, String depositorName, BigDecimal amount) {
    if (payment.getAmount().compareTo(amount) != 0) {
      return false;
    }

    String memberName = payment.getGroupMember().getUser().getName();

    if (memberName.equals(depositorName)) {
      return true;
    }

    String normalizedMemberName = normalizeName(memberName);
    String normalizedDepositorName = normalizeName(depositorName);

    return normalizedMemberName.equals(normalizedDepositorName);
  }

  private String normalizeName(String name) {
    if (name == null) return "";
    return name.replaceAll("[\\s\\-_.]", "").toLowerCase();
  }
}
