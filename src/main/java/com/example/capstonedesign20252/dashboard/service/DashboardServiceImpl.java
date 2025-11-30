package com.example.capstonedesign20252.dashboard.service;

import com.example.capstonedesign20252.dashboard.dto.DashboardResponseDto;
import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.group.repository.GroupRepository;
import com.example.capstonedesign20252.groupMember.repository.GroupMemberRepository;
import com.example.capstonedesign20252.payment.domain.Payment;
import com.example.capstonedesign20252.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

  private final GroupRepository groupRepository;
  private final PaymentRepository paymentRepository;
  private final GroupMemberRepository groupMemberRepository;  // ✅ 추가

  @Override
  @Cacheable(value = "dashboard", key = "#groupId")
  public DashboardResponseDto getDashBoard(Long groupId) {
    log.info("대시보드 데이터 계산 시작 - groupId: {}", groupId);

    Group group = groupRepository.findById(groupId)
                                 .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

    // ✅ 실제 그룹 멤버 수 조회 (Payment가 없어도 멤버 수는 표시)
    int actualMemberCount = (int) groupMemberRepository.countByGroupId(groupId);

    List<Payment> payments = paymentRepository.findByGroupId(groupId);

    if (payments.isEmpty()) {
      log.info("Payment 데이터가 없습니다 - groupId: {}, 멤버 수: {}", groupId, actualMemberCount);
      return DashboardResponseDto.builder()
                                 .groupId(groupId)
                                 .groupName(group.getGroupName())
                                 .fee(group.getFee())
                                 .totalMembers(actualMemberCount)
                                 .paidMembers(0)
                                 .unpaidMembers(actualMemberCount)
                                 .totalAmount(BigDecimal.ZERO)
                                 .paidAmount(BigDecimal.ZERO)
                                 .unpaidAmount(BigDecimal.ZERO)
                                 .paymentRate(0.0)
                                 .recentPayments(List.of())
                                 .lastUpdated(LocalDateTime.now())
                                 .build();
    }

    int totalMembers = payments.size();
    int paidMembers = (int) payments.stream()
                                    .filter(p -> "PAID".equals(p.getStatus()))
                                    .count();
    int unpaidMembers = totalMembers - paidMembers;

    BigDecimal totalAmount = payments.stream()
                                     .map(Payment::getAmount)
                                     .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal paidAmount = payments.stream()
                                    .filter(p -> "PAID".equals(p.getStatus()))
                                    .map(Payment::getAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal unpaidAmount = totalAmount.subtract(paidAmount);

    double paymentRate = totalMembers > 0
        ? (double) paidMembers / totalMembers * 100
        : 0.0;

    List<DashboardResponseDto.RecentPaymentDto> recentPayments = payments.stream()
                 .filter(p -> "PAID".equals(p.getStatus()))
                 .filter(p -> p.getPaidAt() != null)
                 .sorted((p1, p2) -> p2.getPaidAt().compareTo(p1.getPaidAt()))
                 .limit(10)
                 .map(p -> DashboardResponseDto.RecentPaymentDto.builder()
                    .paymentId(p.getId())
                    .memberName(p.getGroupMember().getName())
                    .amount(p.getAmount())
                    .paidAt(p.getPaidAt())
                    .status(p.getStatus())
                    .build())
                 .collect(Collectors.toList());

    return DashboardResponseDto.builder()
                               .groupId(groupId)
                               .groupName(group.getGroupName())
                               .fee(group.getFee())
                               .totalMembers(totalMembers)
                               .paidMembers(paidMembers)
                               .unpaidMembers(unpaidMembers)
                               .totalAmount(totalAmount)
                               .paidAmount(paidAmount)
                               .unpaidAmount(unpaidAmount)
                               .paymentRate(Math.round(paymentRate * 100.0) / 100.0)
                               .recentPayments(recentPayments)
                               .lastUpdated(LocalDateTime.now())
                               .build();
  }

  @Override
  @CacheEvict(value = "dashboard", key = "#groupId")
  public void evictDashboardCache(Long groupId) {
    log.info("대시보드 캐시 삭제 - groupId: {}", groupId);
  }
}
