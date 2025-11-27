package com.example.capstonedesign20252.group.service;

import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.group.dto.GroupResponseDto;
import com.example.capstonedesign20252.group.dto.UpdateRequestGroupDto;
import com.example.capstonedesign20252.group.dto.createGroupRequestDto;
import java.util.List;

public interface GroupService {

  /**
   * 그룹 생성 (멤버 없이)
   * 변경: MultipartFile memberFile 파라미터 제거
   */
  GroupResponseDto createGroup(Long userId, createGroupRequestDto dto);

  GroupResponseDto getGroup(Long groupId);

  List<GroupResponseDto> getAllGroups();

  List<GroupResponseDto> getUserGroups(Long userId);

  void deleteGroup(Long groupId);

  Group findByGroupId(Long groupId);

  GroupResponseDto updateGroup(Long groupId, UpdateRequestGroupDto updateRequestGroupDto);
}