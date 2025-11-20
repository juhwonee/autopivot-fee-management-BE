package com.example.capstonedesign20252.dashboard.service;

import com.example.capstonedesign20252.dashboard.dto.DashboardResponseDto;
import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.group.repository.GroupRepository;
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

  /**
   * 대시보드 데이터 조회 (캐시 사용)
   * - 캐시에 있으면 캐시에서 반환
   * - 없으면 DB에서 계산 후 캐시에 저장
   */
  @Override
  @Cacheable(value = "dashboard", key = "#groupId")
  public DashboardResponseDto getDashBoard(Long groupId) {
    log.info("📊 대시보드 데이터 계산 시작 - groupId: {}", groupId);

    Group group = groupRepository.findById(groupId)
                                 .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

    // 해당 그룹의 모든 결제 정보 조회
    List<Payment> payments = paymentRepository.findByGroupId(groupId);

    // 통계 계산
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

    // 최근 입금 내역 (최근 10건)
    List<DashboardResponseDto.RecentPaymentDto> recentPayments = payments.stream()
                                                                         .filter(p -> "PAID".equals(p.getStatus()))
                                                                         .sorted((p1, p2) -> p2.getPaidAt().compareTo(p1.getPaidAt()))
                                                                         .limit(10)
                                                                         .map(p -> DashboardResponseDto.RecentPaymentDto.builder()
                                                                                                                        .paymentId(p.getId())
                                                                                                                        .memberName(p.getGroupMember().getUser().getName())
                                                                                                                        .amount(p.getAmount())
                                                                                                                        .paidAt(p.getPaidAt())
                                                                                                                        .status(p.getStatus())
                                                                                                                        .build())
                                                                         .collect(Collectors.toList());

    return DashboardResponseDto.builder()
                               .groupId(groupId)
                               .groupName(group.getGroupName())
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

  /**
   * 특정 그룹의 캐시 삭제
   * - PaymentMatchingService에서 호출
   */
  @CacheEvict(value = "dashboard", key = "#groupId")
  public void evictDashboardCache(Long groupId) {
    log.info("🗑️ 대시보드 캐시 삭제 - groupId: {}", groupId);
  }
}