package com.example.capstonedesign20252.service;

import com.example.capstonedesign20252.dto.PaymentRequestDto;

public interface PaymentLogService {
  void savePaymentLog(PaymentRequestDto paymentRequestDto);
}
