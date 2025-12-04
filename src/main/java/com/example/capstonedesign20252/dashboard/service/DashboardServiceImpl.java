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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

  private final GroupRepository groupRepository;
  private final PaymentRepository paymentRepository;
  private final GroupMemberRepository groupMemberRepository;

  @Override
  @Cacheable(value = "dashboard", key = "#groupId")
  public DashboardResponseDto getDashBoard(Long groupId) {
    log.info("대시보드 데이터 계산 시작 - groupId: {}", groupId);

    Group group = groupRepository.findById(groupId)
                                 .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

    int actualMemberCount = (int) groupMemberRepository.countByGroupId(groupId);

    String currentPeriod = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    List<Payment> payments = paymentRepository.findByGroupIdAndPaymentPeriod(groupId, currentPeriod);

    log.info("조회 조건 - groupId: {}, period: {}, 멤버수: {}, Payment수: {}",
        groupId, currentPeriod, actualMemberCount, payments.size());

    if (payments.isEmpty()) {
      log.info("Payment 데이터가 없습니다 - groupId: {}, 멤버 수: {}", groupId, actualMemberCount);
      return DashboardResponseDto.builder()
                                 .groupId(groupId)
                                 .groupName(group.getGroupName())
                                 .fee(group.getFee())
                                 .totalMembers(actualMemberCount)
                                 .paidMembers(0)
                                 .unpaidMembers(actualMemberCount)
                                 .totalAmount(BigDecimal.valueOf((long) group.getFee() * actualMemberCount))
                                 .paidAmount(BigDecimal.ZERO)
                                 .unpaidAmount(BigDecimal.valueOf((long) group.getFee() * actualMemberCount))
                                 .paymentRate(0.0)
                                 .recentPayments(List.of())
                                 .lastUpdated(LocalDateTime.now())
                                 .build();
    }

    int totalMembers = actualMemberCount;

    // 납부 완료 인원 (현재 월 기준)
    int paidMembers = (int) payments.stream()
                                    .filter(p -> "PAID".equals(p.getStatus()))
                                    .count();
    int unpaidMembers = totalMembers - paidMembers;

    BigDecimal totalAmount = BigDecimal.valueOf((long) group.getFee() * totalMembers);

    // 납부 완료 금액
    BigDecimal paidAmount = payments.stream()
                                    .filter(p -> "PAID".equals(p.getStatus()))
                                    .map(Payment::getAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal unpaidAmount = totalAmount.subtract(paidAmount);

    // 납부율 계산
    double paymentRate = totalMembers > 0
        ? (double) paidMembers / totalMembers * 100
        : 0.0;

    // 최근 납부 내역 (전체 기간에서 최근 10건)
    List<Payment> allPayments = paymentRepository.findByGroupId(groupId);
    List<DashboardResponseDto.RecentPaymentDto> recentPayments = allPayments.stream()
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

    log.info("대시보드 계산 완료 - 멤버: {}명, 납부: {}명, 미납: {}명, 납부율: {}%",
        totalMembers, paidMembers, unpaidMembers, Math.round(paymentRate * 100.0) / 100.0);

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
