package com.example.capstonedesign20252.payment.domain;

import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.groupMember.domain.GroupMember;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private Group group;  // 어느 그룹의 회비인지

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_member_id", nullable = false)
  private GroupMember groupMember;  // 납부해야 할 회원

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;  // 납부 금액

  @Column(nullable = false, length = 20)
  private String status;  // PENDING(미납), PAID(납부완료), OVERDUE(연체)

  @Column(name = "due_date")
  private LocalDateTime dueDate;  // 납부 기한

  @Column(name = "paid_at")
  private LocalDateTime paidAt;  // 실제 납부 시간

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;  // 등록 시간

  @Column(name = "payment_period")
  private String paymentPeriod;  // 납부 주기 (예: "2025-01")

  @Builder
  public Payment(Group group, GroupMember groupMember, BigDecimal amount,
      LocalDateTime dueDate, String paymentPeriod) {
    this.group = group;
    this.groupMember = groupMember;
    this.amount = amount;
    this.status = "PENDING";  // 기본값: 미납
    this.dueDate = dueDate;
    this.paymentPeriod = paymentPeriod;
    this.createdAt = LocalDateTime.now();
  }

  // 납부 완료 처리
  public void markAsPaid(LocalDateTime paidAt) {
    this.status = "PAID";
    this.paidAt = paidAt;
  }

  // 수동 납부 처리
  public void manualPaid() {
    this.status = "PAID";
    this.paidAt = LocalDateTime.now();
  }

  // 연체 상태로 변경
  public void markAsOverdue() {
    this.status = "OVERDUE";
  }
}