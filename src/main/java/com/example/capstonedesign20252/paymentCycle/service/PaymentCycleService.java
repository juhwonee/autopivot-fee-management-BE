package com.example.capstonedesign20252.paymentCycle.service;


import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.group.repository.GroupRepository;
import com.example.capstonedesign20252.group.service.GroupService;
import com.example.capstonedesign20252.groupMember.domain.GroupMember;
import com.example.capstonedesign20252.groupMember.domain.GroupMemberErrorCode;
import com.example.capstonedesign20252.groupMember.domain.GroupMemberException;
import com.example.capstonedesign20252.groupMember.repository.GroupMemberRepository;
import com.example.capstonedesign20252.payment.domain.Payment;
import com.example.capstonedesign20252.payment.repository.PaymentRepository;
import com.example.capstonedesign20252.paymentCycle.domain.PaymentCycle;
import com.example.capstonedesign20252.paymentCycle.domain.PaymentCycleErrorCode;
import com.example.capstonedesign20252.paymentCycle.domain.PaymentCycleException;
import com.example.capstonedesign20252.paymentCycle.dto.ActiveCycleResponseDto;
import com.example.capstonedesign20252.paymentCycle.dto.PaymentCycleResponseDto;
import com.example.capstonedesign20252.paymentCycle.dto.StartPaymentCycleRequestDto;
import com.example.capstonedesign20252.paymentCycle.repository.PaymentCycleRepository;
import java.math.BigDecimal;
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
@Transactional(readOnly = true)
public class PaymentCycleService {
  private final PaymentCycleRepository paymentCycleRepository;
  private final GroupRepository groupRepository;
  private final GroupMemberRepository groupMemberRepository;
  private final PaymentRepository paymentRepository;
  private final GroupService groupService;

  @Transactional
  public PaymentCycleResponseDto startPaymentCycle(Long groupId, StartPaymentCycleRequestDto request){
    log.info("회비 수금 시작 - groupId: {}, period: {}", groupId, request.period());

    Group group = groupService.findByGroupId(groupId);
    Optional<PaymentCycle> existingActive = paymentCycleRepository
        .findByGroupIdAndStatus(groupId, "ACTIVE");

    if(existingActive.isPresent()){
      throw new PaymentCycleException(PaymentCycleErrorCode.ALREADY_ACTIVE_CYCLE);
    }

    List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);

    if (members.isEmpty()){
      throw new GroupMemberException(GroupMemberErrorCode.MEMBER_NOT_FOUND);
    }

    PaymentCycle cycle = PaymentCycle.builder()
                                     .group(group)
                                     .period(request.period())
                                     .startDate(LocalDateTime.now())
                                     .dueDate(request.dueDate())
                                     .totalMembers(members.size())
                                     .monthlyFee(group.getFee())
                                     .build();
    paymentCycleRepository.save(cycle);

    for (GroupMember member : members) {
      Payment payment = Payment.builder()
                               .group(group)
                               .groupMember(member)
                               .amount(new BigDecimal(group.getFee()))
                               .dueDate(request.dueDate())
                               .paymentPeriod(request.period())
                               .build();
      paymentRepository.save(payment);
    }

    log.info("회비 수금 시작 완료 - cycleId: {}, 멤버 수: {}", cycle.getId(), members.size());
    return PaymentCycleResponseDto.from(cycle);
  }

  @Transactional
  public PaymentCycleResponseDto closePaymentCycle(Long groupId, Long cycleId) {
    log.info("회비 수금 종료 - groupId: {}, cycleId: {}", groupId, cycleId);

    PaymentCycle cycle = paymentCycleRepository.findById(cycleId)
                                               .orElseThrow(() -> new PaymentCycleException(PaymentCycleErrorCode.NOT_FOUND_CYCLE));

    if(!cycle.getGroup().getId().equals(groupId)){
      throw new PaymentCycleException(PaymentCycleErrorCode.NOT_CYCLE_PERIOD);
    }

    if(!cycle.isActive()){
      throw new PaymentCycleException(PaymentCycleErrorCode.ALREADY_FINISH_CYCLE);
    }

    cycle.close();

    List<Payment> pendingPayments = paymentRepository
        .findByGroupIdAndPaymentPeriodAndStatus(groupId, cycle.getPeriod(), "PENDING");

    for (Payment payment : pendingPayments){
      payment.markAsOverdue();
    }

    log.info("회비 수금 종료 완료 - cycleId: {}, 연체 처리: {}명", cycleId, pendingPayments.size());
    return PaymentCycleResponseDto.from(cycle);
  }

  public ActiveCycleResponseDto getActiveCycle(Long groupId){
    Optional<PaymentCycle> cycleOpt = paymentCycleRepository
        .findByGroupIdAndStatus(groupId, "ACTIVE");

    if (cycleOpt.isEmpty()){
      return new ActiveCycleResponseDto(
          false, null, null, null, null, null,
          null, null, null, null, null, null, null);
    }

    PaymentCycle cycle = cycleOpt.get();
    List<Payment> payments = paymentRepository
        .findByGroupIdAndPaymentPeriod(groupId, cycle.getPeriod());

    int paidMembers = (int) payments.stream()
                                    .filter(p -> "PAID".equals(p.getStatus()))
                                    .count();
    int unpaidMembers = cycle.getTotalMembers() - paidMembers;

    long totalCollected = payments.stream()
                                  .filter(p -> "PAID".equals(p.getStatus()))
                                  .mapToLong(p -> p.getAmount().longValue())
                                  .sum();

    int paymentRate = cycle.getTotalMembers() == 0 ? 0
        : (paidMembers * 100) / cycle.getTotalMembers();

    return new ActiveCycleResponseDto(
        true,
        cycle.getId(),
        cycle.getPeriod(),
        cycle.getStartDate(),
        cycle.getDueDate(),
        cycle.getGroup().getAccountName(),
        cycle.getTotalMembers(),
        cycle.getMonthlyFee(),
        (long) cycle.getTotalMembers() * cycle.getMonthlyFee(),
        paidMembers,
        unpaidMembers,
        totalCollected,
        paymentRate
    );
  }

  public List<PaymentCycleResponseDto> getCycleHistory(Long groupId) {
    return paymentCycleRepository.findByGroupIdOrderByCreatedAtDesc(groupId)
                                 .stream()
                                 .map(PaymentCycleResponseDto::from)
                                 .toList();
  }
}
