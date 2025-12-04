package com.example.capstonedesign20252.paymentCycle.domain;

import com.example.capstonedesign20252.group.domain.Group;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "payment_cycles", indexes = {
    @Index(name = "idx_group_status", columnList = "group_id, status"),
    @Index(name = "idx_group_period", columnList = "group_id, period")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaymentCycle {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private Group group;

  @Column(name = "period", length = 7, nullable = false)
  private String period;

  @Column(name = "status", length = 20, nullable = false)
  @Builder.Default
  private String status = "ACTIVE";

  @Column(name = "start_date", nullable = false)
  private LocalDateTime startDate;

  @Column(name = "due_date")
  private LocalDateTime dueDate;

  @Column(name = "closed_at")
  private LocalDateTime closedAt;

  @Column(name = "total_members")
  private Integer totalMembers;

  @Column(name = "monthly_fee")
  private Integer monthlyFee;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  public void close() {
    this.status = "CLOSED";
    this.closedAt = LocalDateTime.now();
  }
  public boolean isActive() {
    return "ACTIVE".equals(this.status);
  }
}
