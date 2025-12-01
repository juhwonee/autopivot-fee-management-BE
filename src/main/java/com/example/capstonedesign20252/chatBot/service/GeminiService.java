package com.example.capstonedesign20252.chatBot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeminiService {

  @Value("${gemini.api-key}")
  private String apiKey;

  private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta";
  private static final String MODEL_NAME = "gemini-2.0-flash";

  private final WebClient webClient;
  private final ObjectMapper objectMapper;

  public GeminiService() {
    DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
    factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

    this.webClient = WebClient.builder()
                              .uriBuilderFactory(factory)
                              .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                              .build();
    this.objectMapper = new ObjectMapper();
  }

  @PostConstruct
  public void init() {
    log.info("--------------------------------------------------");
    log.info("Gemini 서비스 초기화 및 모델 확인 시작");
    checkAvailableModels();
    log.info("--------------------------------------------------");
  }

  /**
   * 사용 가능한 모델 목록 확인
   */
  public void checkAvailableModels() {
    try {
      if (apiKey == null || apiKey.trim().isEmpty() || apiKey.startsWith("여기에")) {
        log.warn("API 키 확인 필요: application.properties를 확인하세요.");
        return;
      }

      String listUrl = BASE_URL + "/models?key=" + apiKey.trim();
      String response = webClient.get()
                                 .uri(listUrl)
                                 .retrieve()
                                 .bodyToMono(String.class)
                                 .block();

      log.info("API 연결 성공! (키 검증 완료)");

      JsonNode root = objectMapper.readTree(response);
      if (root.has("models")) {
        log.info("[사용 가능한 모델 목록]");
        for (JsonNode model : root.get("models")) {
          String name = model.get("name").asText();
          if (model.has("supportedGenerationMethods") &&
              model.get("supportedGenerationMethods").toString().contains("generateContent")) {
            log.info("{}", name);
          }
        }
      }

    } catch (Exception e) {
      log.error("초기화 중 오류 발생", e);
    }
  }

  /**
   * Gemini API를 통한 채팅 처리
   *
   * @param systemPrompt 시스템 프롬프트 (AI 역할 정의)
   * @param userMessage 사용자 메시지
   * @return AI 응답 문자열
   */
  public String chat(String systemPrompt, String userMessage) {
    try {
      String cleanKey = apiKey.trim();

      // 1. URL 생성 (인코딩 모드가 NONE이라 문자열 그대로 전송)
      String fullUrl = BASE_URL + "/models/" + MODEL_NAME + ":generateContent?key=" + cleanKey;

      log.info("🤖 Gemini 요청 시작: {}", MODEL_NAME);

      // 2. 프롬프트 구성
      String fullPrompt = (systemPrompt == null || systemPrompt.isEmpty())
          ? userMessage
          : systemPrompt + "\n\nUser Query: " + userMessage;

      Map<String, Object> requestBody = Map.of(
          "contents", List.of(
              Map.of("parts", List.of(Map.of("text", fullPrompt)))
          ),
          "generationConfig", Map.of(
              "temperature", 0.9,
              "maxOutputTokens", 1000
          )
      );

      // 3. 요청 전송
      String response = webClient.post()
                                 .uri(fullUrl)
                                 .header("Content-Type", "application/json")
                                 .bodyValue(requestBody)
                                 .retrieve()
                                 .bodyToMono(String.class)
                                 .block();

      // 4. 응답 파싱
      JsonNode jsonNode = objectMapper.readTree(response);
      if (jsonNode.has("candidates") && !jsonNode.get("candidates").isEmpty()) {
        String aiResponse = jsonNode.get("candidates").get(0)
                                    .get("content").get("parts").get(0)
                                    .get("text").asText();
        log.info("🤖 Gemini 응답 완료");
        return aiResponse;
      } else {
        return "AI 응답이 비어있습니다.";
      }

    } catch (WebClientResponseException e) {
      log.error("HTTP 오류 ({}): {}", e.getStatusCode(), e.getStatusText());
      log.error("반환된 에러 메시지: {}", e.getResponseBodyAsString());
      return "오류 발생: " + e.getStatusCode();
    } catch (Exception e) {
      log.error("시스템 오류", e);
      return "시스템 오류: " + e.getMessage();
    }
  }
}
