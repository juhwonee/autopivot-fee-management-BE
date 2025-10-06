package com.example.capstonedesigin20252.controller;

import com.example.capstonedesigin20252.dto.PaymentRequestDto;
import com.example.capstonedesigin20252.service.PaymentLogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentLogController {

  private final PaymentLogService paymentLogService;
  private static final Logger logger = LoggerFactory.getLogger(PaymentLogController.class);

  @PostMapping("/api/payments/log")
  public ResponseEntity<Void> logPayment(@RequestBody PaymentRequestDto requestDto) {
    try {
      paymentLogService.savePaymentLog(requestDto);
      logger.info("✅ [저장 성공] 결제 정보 DB 저장 완료: {}", requestDto.name());
      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException e) {
      logger.error("❌ [저장 실패] 유효하지 않은 데이터: {}", e.getMessage());
      return ResponseEntity.badRequest().build(); // 400 Bad Request 응답
    } catch (Exception e) {
      logger.error("❌ [저장 실패] 서버 내부 오류: {}", e.getMessage());
      return ResponseEntity.internalServerError().build(); // 500 Internal Server Error 응답
    }
  }
}