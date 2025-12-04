package com.example.capstonedesign20252.fee.service;

import com.example.capstonedesign20252.fee.dto.FeesResponseDto;
import com.example.capstonedesign20252.fee.dto.MemberPaymentDto;
import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.group.repository.GroupRepository;
import com.example.capstonedesign20252.group.service.GroupService;
import com.example.capstonedesign20252.groupMember.domain.GroupMember;
import com.example.capstonedesign20252.groupMember.repository.GroupMemberRepository;
import com.example.capstonedesign20252.payment.domain.Payment;
import com.example.capstonedesign20252.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeeService {

  private final GroupService groupService;
  private final GroupMemberRepository groupMemberRepository;
  private final PaymentRepository paymentRepository;

  public FeesResponseDto getFeesStatus(Long groupId, String period) {

    Group group = groupService.findByGroupId(groupId);
    List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);

    List<Payment> payments = paymentRepository.findByGroupIdAndPaymentPeriod(groupId, period);
    Map<Long, Payment> paymentMap = payments.stream()
                                            .collect(Collectors.toMap(
                                                p -> p.getGroupMember().getId(),
                                                p -> p,
                                                (p1, p2) -> p1
                                            ));

    LocalDateTime now = LocalDateTime.now();
    List<MemberPaymentDto> memberPayments = members.stream()
                                                   .map(member -> {
                                                     Payment payment = paymentMap.get(member.getId());

                                                     if (payment == null) {
                                                       return new MemberPaymentDto(
                                                           member.getId(),
                                                           null,
                                                           member.getName(),
                                                           member.getPhone(),
                                                           0,
                                                           "PENDING",
                                                           null
                                                       );
                                                     }

                                                     String status = payment.getStatus();

                                                     if ("PENDING".equals(status) && payment.getDueDate() != null
                                                         && payment.getDueDate().isBefore(now)) {
                                                       status = "OVERDUE";
                                                     }

                                                     return new MemberPaymentDto(
                                                         member.getId(),
                                                         payment.getId(),
                                                         member.getName(),
                                                         member.getPhone(),
                                                         payment.getAmount().intValue(),
                                                         status,
                                                         payment.getPaidAt()
                                                     );
                                                   })
                                                   .collect(Collectors.toList());

    int totalMembers = members.size();
    int paidMembers = (int) memberPayments.stream()
                                          .filter(m -> "PAID".equals(m.status()))
                                          .count();
    int unpaidMembers = totalMembers - paidMembers;

    long totalCollected = memberPayments.stream()
                                        .filter(m -> "PAID".equals(m.status()))
                                        .mapToLong(MemberPaymentDto::paidAmount)
                                        .sum();

    long targetAmount = (long) group.getFee() * totalMembers;
    int paymentRate = totalMembers == 0 ? 0 : (int) ((paidMembers * 100) / totalMembers);

    log.info("회비 현황 조회 완료 - 납부율: {}%, 납부: {}명, 미납: {}명",
        paymentRate, paidMembers, unpaidMembers);

    return new FeesResponseDto(
        group.getGroupName(),
        group.getFee(),
        period,
        totalMembers,
        paidMembers,
        unpaidMembers,
        totalCollected,
        targetAmount,
        paymentRate,
        memberPayments
    );
  }
}
