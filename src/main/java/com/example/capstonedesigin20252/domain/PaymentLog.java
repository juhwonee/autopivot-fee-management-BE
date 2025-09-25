package com.example.capstonedesigin20252.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "amount", nullable = false)
  private Integer amount;

  @Column(name = "target_account", nullable = false)
  private String targetAccount;

  @Builder
  public PaymentLog(Long id, String name, Integer amount, String targetAccount){
    if(amount != null){
      if(amount <= 0) throw new IllegalArgumentException("송금 금액은 0원일 수 없습니다.");
    }
    this.id = id;
    this.name = name;
    this.amount = amount;
    this.targetAccount = targetAccount;
  }
}
