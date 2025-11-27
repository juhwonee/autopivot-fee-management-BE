package com.example.capstonedesign20252.group.service;

import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.group.domain.GroupErrorCode;
import com.example.capstonedesign20252.group.domain.GroupException;
import com.example.capstonedesign20252.group.dto.GroupResponseDto;
import com.example.capstonedesign20252.group.dto.UpdateRequestGroupDto;
import com.example.capstonedesign20252.group.dto.createGroupRequestDto;
import com.example.capstonedesign20252.group.repository.GroupRepository;
import com.example.capstonedesign20252.groupMember.domain.GroupMember;
import com.example.capstonedesign20252.groupMember.repository.GroupMemberRepository;
import com.example.capstonedesign20252.user.domain.User;
import com.example.capstonedesign20252.user.exception.UserErrorCode;
import com.example.capstonedesign20252.user.exception.UserException;
import com.example.capstonedesign20252.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final GroupMemberRepository groupMemberRepository;

  @Transactional
  public GroupResponseDto createGroup(Long userId, createGroupRequestDto dto) {

    User user = userRepository.findById(userId)
                              .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    Group group = Group.builder()
                       .user(user)
                       .groupName(dto.groupName())
                       .accountName(dto.accountName())
                       .description(dto.description())
                       .groupCategory(dto.groupCategory())
                       .fee(dto.fee())
                       .build();

    Group savedGroup = groupRepository.save(group);
    log.info("그룹 생성 완료: {} (ID: {})", savedGroup.getGroupName(), savedGroup.getId());

    GroupMember adminMember = GroupMember.builder()
                                         .group(savedGroup)
                                         .name(user.getName())
                                         .email(user.getEmail())
                                         .phone(user.getPhone())
                                         .isAdmin(true)
                                         .build();
    groupMemberRepository.save(adminMember);
    log.info("그룹 관리자 추가: {} ({})", user.getName(), user.getEmail());

    return GroupResponseDto.from(savedGroup);
  }

  @Override
  public GroupResponseDto getGroup(Long groupId) {
    Group group = groupRepository.findById(groupId)
                                 .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));
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
    Group group = groupRepository.findById(groupId)
        .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

    groupRepository.delete(group);
  }

  @Override
  public Group findByGroupId(Long groupId){
    return groupRepository.findById(groupId)
                          .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));
  }

  @Override
  @Transactional
  public GroupResponseDto updateGroup(Long groupId, UpdateRequestGroupDto updateRequestGroupDto) {

    Group group = groupRepository.findById(groupId)
        .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

    group.updateGroup(updateRequestGroupDto);
    return GroupResponseDto.from(group);
  }

  private GroupResponseDto toDto(Group group){
    return new GroupResponseDto(
        group.getId(),
        group.getUser().getId(),
        group.getGroupName(),
        group.getAccountName(),
        group.getDescription(),
        group.getGroupCategory(),
        group.getFee());
  }
}
