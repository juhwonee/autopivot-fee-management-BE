package com.example.capstonedesign20252.chatBot.dto;

public record ChatRequestDto(
  String message,
  String sessionId //redis 연결 때 대화 이력 저장으로 이용.
){
}