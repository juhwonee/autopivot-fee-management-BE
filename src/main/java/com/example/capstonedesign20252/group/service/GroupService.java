package com.example.capstonedesign20252.group.service;


import com.example.capstonedesign20252.group.dto.GroupResponseDto;
import com.example.capstonedesign20252.group.dto.createGroupRequestDto;
import java.util.List;

public interface GroupService{
  GroupResponseDto createGroup(Long userId, createGroupRequestDto dto);
  GroupResponseDto getGroup(Long groupId);
  List<GroupResponseDto> getAllGroups();
  void deleteGroup(Long groupId);
}