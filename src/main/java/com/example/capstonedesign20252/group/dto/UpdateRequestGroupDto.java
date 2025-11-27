package com.example.capstonedesign20252.group.dto;

import com.example.capstonedesign20252.group.domain.GroupCategory;

public record UpdateRequestGroupDto(
    String groupName,
    String accountName,
    String description,
    GroupCategory groupCategory,
    Integer fee
) {
}
