package com.example.capstonedesign20252.chatBot.dto;

public record ChatResponseDto(
  String response,
  String type,
  Object data
){
}