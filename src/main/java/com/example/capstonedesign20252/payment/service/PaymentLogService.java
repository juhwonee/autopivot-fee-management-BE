package com.example.capstonedesign20252.payment.service;

import com.example.capstonedesign20252.payment.domain.PaymentLog;
import com.example.capstonedesign20252.payment.dto.PaymentRequestDto;

public interface PaymentLogService {
  PaymentLog savePaymentLog(PaymentRequestDto paymentRequestDto);
}
