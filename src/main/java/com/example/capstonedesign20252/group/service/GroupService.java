package com.example.capstonedesign20252.group.service;


import com.example.capstonedesign20252.group.dto.GroupResponseDto;
import com.example.capstonedesign20252.group.dto.createGroupRequestDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface GroupService{
  GroupResponseDto createGroup(Long userId, createGroupRequestDto dto, MultipartFile memberFile);
  GroupResponseDto getGroup(Long groupId);
  List<GroupResponseDto> getAllGroups();
  List<GroupResponseDto> getUserGroups(Long userId);
  void deleteGroup(Long groupId);
}