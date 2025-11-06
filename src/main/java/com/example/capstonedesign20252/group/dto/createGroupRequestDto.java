package com.example.capstonedesign20252.group.dto;

public record createGroupRequestDto(
    String groupName,
    String description,
    Integer fee
) {
}
