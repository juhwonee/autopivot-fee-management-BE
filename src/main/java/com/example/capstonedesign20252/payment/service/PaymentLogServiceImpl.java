package com.example.capstonedesign20252.payment.service;

import com.example.capstonedesign20252.payment.domain.PaymentLog;
import com.example.capstonedesign20252.payment.dto.PaymentRequestDto;
import com.example.capstonedesign20252.payment.repository.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentLogServiceImpl implements PaymentLogService {

  private final PaymentLogRepository paymentLogRepository;

  @Transactional
  public PaymentLog savePaymentLog(PaymentRequestDto paymentRequestDto){
    PaymentLog paymentLog = paymentRequestDto.toEntity();
    return paymentLogRepository.save(paymentLog);
  }
}
