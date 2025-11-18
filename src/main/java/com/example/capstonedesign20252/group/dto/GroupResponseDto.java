package com.example.capstonedesign20252.group.dto;

import com.example.capstonedesign20252.group.domain.GroupCategory;

public record GroupResponseDto(
    Long groupId,
    Long leaderId,
    String groupName,
    String description,
    GroupCategory groupCategory,
    Integer fee
) {
}
