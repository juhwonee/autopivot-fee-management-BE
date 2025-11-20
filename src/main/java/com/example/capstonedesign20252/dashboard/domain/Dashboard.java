package com.example.capstonedesign20252.dashboard.domain;

import com.example.capstonedesign20252.group.domain.Group;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dashboard {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "group_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Group group;

  @Column(name = "paid_count", nullable = false)
  private Integer paidCount = 0;

  @Column(name = "unpaid_count", nullable = false)
  private Integer unpaidCount = 0;

  @Column(name = "total_fee", nullable = false)
  private Integer totalFee = 0;

  @Builder
  public Dashboard(Group group, Integer paidCount, Integer unpaidCount, Integer totalFee){
    this.group = group;
    if(paidCount < 0) throw new IllegalArgumentException("paidCount가 음수입니다.");
    this.paidCount = paidCount;
    if(unpaidCount < 0) throw new IllegalArgumentException("unpaidCount가 음수입니다.");
    this.unpaidCount = unpaidCount;
    if(totalFee < 0) throw new IllegalArgumentException("totalFee가 음수입니다.");
    this.totalFee = totalFee;
  }
}
