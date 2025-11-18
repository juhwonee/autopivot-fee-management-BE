package com.example.capstonedesign20252.group.dto;

import com.example.capstonedesign20252.group.domain.GroupCategory;

public record createGroupRequestDto(
    String groupName,
    String description,
    GroupCategory groupCategory,
    Integer fee
) {
}
