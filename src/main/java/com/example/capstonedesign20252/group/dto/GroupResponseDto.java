package com.example.capstonedesign20252.group.dto;

import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.group.domain.GroupCategory;

public record GroupResponseDto(
    Long groupId,
    Long leaderId,
    String groupName,
    String description,
    GroupCategory groupCategory,
    Integer fee
) {
  public static GroupResponseDto from (Group group){
    return new GroupResponseDto(
        group.getId(),
        group.getUser().getId(),
        group.getGroupName(),
        group.getDescription(),
        group.getGroupCategory(),
        group.getFee()
    );
  }
}
