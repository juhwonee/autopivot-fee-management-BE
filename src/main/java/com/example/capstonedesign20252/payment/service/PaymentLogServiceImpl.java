package com.example.capstonedesign20252.payment.service;

import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.group.repository.GroupRepository;
import com.example.capstonedesign20252.groupMember.domain.GroupMember;
import com.example.capstonedesign20252.groupMember.repository.GroupMemberRepository;
import com.example.capstonedesign20252.payment.domain.Payment;
import com.example.capstonedesign20252.payment.domain.PaymentLog;
import com.example.capstonedesign20252.payment.dto.PaymentRequestDto;
import com.example.capstonedesign20252.payment.repository.PaymentLogRepository;
import com.example.capstonedesign20252.payment.repository.PaymentRepository;
import com.example.capstonedesign20252.paymentCycle.domain.PaymentCycle;
import com.example.capstonedesign20252.paymentCycle.repository.PaymentCycleRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentLogServiceImpl implements PaymentLogService {

  private final PaymentLogRepository paymentLogRepository;
  private final PaymentCycleRepository paymentCycleRepository;
  private final PaymentRepository paymentRepository;
  private final GroupMemberRepository groupMemberRepository;
  private final GroupRepository groupRepository;

  @Transactional
  public void savePaymentLog(PaymentRequestDto requestDto) {
    log.info("입금 알림 수신 - name: {}, amount: {}, accountName: {}",
        requestDto.name(), requestDto.amount(), requestDto.targetAccount());

    PaymentLog paymentLog = PaymentLog.builder()
                                      .name(requestDto.name())
                                      .amount(requestDto.amount())
                                      .targetAccount(requestDto.targetAccount())
                                      .receivedAt(requestDto.receivedAt() != null ? requestDto.receivedAt() : LocalDateTime.now())
                                      .build();
    paymentLogRepository.save(paymentLog);

    log.info("PaymentLog 저장 완료 - logId: {}", paymentLog.getId());

    // 1. 그룹 매칭
    Optional<Group> groupOpt = groupRepository
        .findByAccountName(requestDto.targetAccount());

    if (groupOpt.isEmpty()) {
      log.info("매칭되는 그룹 없음 - accountName: '{}', 매칭 스킵", requestDto.targetAccount());
      return;
    }

    Group group = groupOpt.get();
    log.info("그룹 매칭 성공 - groupId: {}, groupName: {}", group.getId(), group.getGroupName());

    // 2. 활성 수금 기간 매칭
    Optional<PaymentCycle> activeCycleOpt = paymentCycleRepository
        .findByGroupIdAndStatus(group.getId(), "ACTIVE");

    if (activeCycleOpt.isEmpty()) {
      log.info("활성화된 수금 기간 없음 - groupId: {}, 매칭 스킵", group.getId());
      return;
    }

    PaymentCycle cycle = activeCycleOpt.get();
    log.info("수금 기간 매칭 - cycleId: {}, period: {}", cycle.getId(), cycle.getPeriod());

    // 3. 동명이인 처리: 해당 이름의 모든 멤버 조회
    List<GroupMember> members = groupMemberRepository
        .findAllByGroupIdAndName(group.getId(), requestDto.name());

    if (members.isEmpty()) {
      log.warn("멤버 매칭 실패 - groupId: {}, name: '{}'", group.getId(), requestDto.name());
      return;
    }

    log.info("동명이인 확인 - name: '{}', 찾은 멤버 수: {}", requestDto.name(), members.size());

    // 4. 미납자 우선 매칭: PENDING 상태인 Payment가 있는 멤버 찾기
    GroupMember matchedMember = null;
    Payment matchedPayment = null;

    for (GroupMember member : members) {
      Optional<Payment> paymentOpt = paymentRepository
          .findByGroupMemberIdAndPaymentPeriodAndStatus(
              member.getId(), cycle.getPeriod(), "PENDING");

      if (paymentOpt.isPresent()) {
        matchedMember = member;
        matchedPayment = paymentOpt.get();
        log.info("미납자 매칭 성공 - memberId: {}, name: {}", member.getId(), member.getName());
        break;
      } else {
        log.info("이미 납부 완료 - memberId: {}, name: {}", member.getId(), member.getName());
      }
    }

    // 5. 매칭된 미납자가 없으면 종료
    if (matchedMember == null || matchedPayment == null) {
      log.warn("PENDING Payment 없음 - 동명이인 {}명 모두 이미 납부 완료 또는 Payment 없음", members.size());
      return;
    }

    // 6. 납부 처리
    int requiredAmount = matchedPayment.getAmount().intValue();
    int paidAmount = requestDto.amount();

    if (paidAmount >= requiredAmount) {
      LocalDateTime paidAt = requestDto.receivedAt() != null ?
          requestDto.receivedAt() : LocalDateTime.now();
      matchedPayment.markAsPaid(paidAt);
      paymentLog.markAsProcessed(matchedPayment.getId());

      log.info("납부 완료 처리 - member: {}, amount: {}, paymentId: {}",
          matchedMember.getName(), paidAmount, matchedPayment.getId());
    } else {
      log.info("부분 납부 감지 - member: {}, paid: {}, required: {}",
          matchedMember.getName(), paidAmount, requiredAmount);
    }
  }
}
