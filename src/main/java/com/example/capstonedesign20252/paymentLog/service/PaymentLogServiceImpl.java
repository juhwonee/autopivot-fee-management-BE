package com.example.capstonedesign20252.paymentLog.service;

import com.example.capstonedesign20252.paymentLog.domain.PaymentLog;
import com.example.capstonedesign20252.paymentLog.dto.PaymentRequestDto;
import com.example.capstonedesign20252.paymentLog.repository.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentLogServiceImpl implements PaymentLogService {

  private final PaymentLogRepository paymentLogRepository;

  @Transactional
  public void savePaymentLog(PaymentRequestDto paymentRequestDto){
    PaymentLog paymentLog = paymentRequestDto.toEntity();
    paymentLogRepository.save(paymentLog);
  }
}
