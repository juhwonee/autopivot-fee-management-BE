package com.example.capstonedesign20252.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class DashboardResponseDto {
  private Long groupId;
  private String groupName;
  private Integer fee;
  private int totalMembers;
  private int paidMembers;
  private int unpaidMembers;
  private BigDecimal totalAmount;
  private BigDecimal paidAmount;
  private BigDecimal unpaidAmount;
  private double paymentRate;
  private List<RecentPaymentDto> recentPayments;
  private LocalDateTime lastUpdated;

  @Getter
  @Builder
  public static class RecentPaymentDto {
    private Long paymentId;
    private String memberName;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private String status;
  }
}
