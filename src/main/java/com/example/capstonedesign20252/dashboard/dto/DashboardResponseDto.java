package com.example.capstonedesign20252.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record DashboardResponseDto(
    Long groupId,
    String groupName,
    Integer totalMembers,
    Integer paidMembers,
    Integer unpaidMembers,
    BigDecimal totalAmount,
    BigDecimal paidAmount,
    BigDecimal unpaidAmount,
    Double paymentRate,
    List<RecentPaymentDto> recentPayments,
    LocalDateTime lastUpdated
) {

  @Builder
  public record RecentPaymentDto(
      Long paymentId,
      String memberName,
      BigDecimal amount,
      LocalDateTime paidAt,
      String status
  ) {}
}