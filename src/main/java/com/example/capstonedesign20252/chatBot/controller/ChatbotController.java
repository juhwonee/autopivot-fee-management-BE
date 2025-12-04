package com.example.capstonedesign20252.chatBot.controller;

import com.example.capstonedesign20252.chatBot.dto.ChatRequestDto;
import com.example.capstonedesign20252.chatBot.dto.ChatResponseDto;
import com.example.capstonedesign20252.chatBot.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class ChatbotController {

  private final ChatbotService chatbotService;

  @PostMapping("/{groupId}/chatbot/message")
  public ResponseEntity<ChatResponseDto> sendMessage(
      @PathVariable Long groupId,
      @RequestBody ChatRequestDto request
  ) {
    log.info("=== 챗봇 메시지 수신 (Group {}): {}", groupId, request.message());

    ChatResponseDto response = chatbotService.processMessage(groupId, request.message());

    log.info("=== 챗봇 응답 전송 (Group {}): {}", groupId, response.response());
    return ResponseEntity.ok(response);
  }
}
