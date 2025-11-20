package com.example.capstonedesign20252.payment.dto;

import com.example.capstonedesign20252.payment.domain.PaymentLog;

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
