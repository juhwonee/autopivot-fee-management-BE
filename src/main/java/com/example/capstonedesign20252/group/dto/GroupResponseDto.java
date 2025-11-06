package com.example.capstonedesign20252.group.dto;

public record GroupResponseDto(
    Long groupId,
    Long leaderId,
    String groupName,
    String description,
    Integer fee
) {
}
