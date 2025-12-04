package com.example.capstonedesign20252.sms.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsService {

  @Value("${coolsms.api-key}")
  private String apiKey;

  @Value("${coolsms.api-secret}")
  private String apiSecret;

  @Value("${coolsms.from-number}")
  private String fromNumber;

  private DefaultMessageService messageService;

  @PostConstruct
  public void init() {
    this.messageService = NurigoApp.INSTANCE
        .initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
  }

  public void send(String to, String text) {
    Message message = new Message();
    message.setFrom(fromNumber);
    message.setTo(to.replaceAll("-", ""));  // 하이픈 제거
    message.setText(text);

    SingleMessageSentResponse response = messageService.sendOne(
        new SingleMessageSendingRequest(message)
    );

    log.info("SMS 발송 완료 - to: {}, messageId: {}", to, response.getMessageId());
  }
}
