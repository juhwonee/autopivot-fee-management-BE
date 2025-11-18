package com.example.capstonedesign20252.group.service;

import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.group.dto.GroupResponseDto;
import com.example.capstonedesign20252.group.dto.createGroupRequestDto;
import com.example.capstonedesign20252.group.repository.GroupRepository;
import com.example.capstonedesign20252.user.domain.User;
import com.example.capstonedesign20252.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public GroupResponseDto createGroup(Long userId, createGroupRequestDto dto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

    Group group = Group.builder()
        .user(user)
        .groupName(dto.groupName())
        .description(dto.description())
        .fee(dto.fee())
        .build();

    Group savedGroup = groupRepository.save(group);
    return toDto(savedGroup);
  }

  @Override
  public GroupResponseDto getGroup(Long groupId) {
    Group group = groupRepository.findById(groupId)
        .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));
    return toDto(group);
  }

  @Override
  public List<GroupResponseDto> getAllGroups() {
    return groupRepository.findAll()
        .stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  public List<GroupResponseDto> getUserGroups(Long userId) {
    return groupRepository.findByUserId(userId)
        .stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  @Transactional
  public void deleteGroup(Long groupId) {
    groupRepository.deleteById(groupId);
  }

  private GroupResponseDto toDto(Group group){
    return new GroupResponseDto(
        group.getId(),
        group.getUser().getId(),
        group.getGroupName(),
        group.getDescription(),
        group.getGroupCategory(),
        group.getFee());
  }
}
