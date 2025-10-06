package com.example.capstonedesigin20252.dto;

import com.example.capstonedesigin20252.domain.PaymentLog;

public record PaymentRequestDto(
    String name,
    Integer amount,
    String targetAccount
) {
  public PaymentLog toEntity(){
    return PaymentLog.builder()
        .name(this.name)
        .amount(this.amount)
        .targetAccount(this.targetAccount)
        .build();
  }
}
