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
  private Group group;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_member_id", nullable = false)
  private GroupMember groupMember;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 20)
  private String status;

  @Column(name = "due_date")
  private LocalDateTime dueDate;

  @Column(name = "paid_at")
  private LocalDateTime paidAt;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "payment_period")
  private String paymentPeriod;

  @Builder
  public Payment(Group group, GroupMember groupMember, BigDecimal amount,
      LocalDateTime dueDate, String paymentPeriod) {
    this.group = group;
    this.groupMember = groupMember;
    this.amount = amount;
    this.status = "PENDING";
    this.dueDate = dueDate;
    this.paymentPeriod = paymentPeriod;
    this.createdAt = LocalDateTime.now();
  }

  public void markAsPaid(LocalDateTime paidAt) {
    this.status = "PAID";
    this.paidAt = paidAt;
  }

  public void manualPaid() {
    this.status = "PAID";
    this.paidAt = LocalDateTime.now();
  }

  public void markAsOverdue() {
    this.status = "OVERDUE";
  }
}
