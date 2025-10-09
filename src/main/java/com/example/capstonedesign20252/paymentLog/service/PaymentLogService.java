package com.example.capstonedesign20252.paymentLog.service;

import com.example.capstonedesign20252.paymentLog.dto.PaymentRequestDto;

public interface PaymentLogService {
  void savePaymentLog(PaymentRequestDto paymentRequestDto);
}
