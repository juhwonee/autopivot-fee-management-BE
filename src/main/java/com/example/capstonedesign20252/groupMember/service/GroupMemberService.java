package com.example.capstonedesign20252.groupMember.service;

import com.example.capstonedesign20252.excel.dto.MemberDataDto;
import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.group.domain.GroupErrorCode;
import com.example.capstonedesign20252.group.domain.GroupException;
import com.example.capstonedesign20252.groupMember.domain.GroupMember;
import com.example.capstonedesign20252.group.repository.GroupRepository;
import com.example.capstonedesign20252.groupMember.domain.GroupMemberErrorCode;
import com.example.capstonedesign20252.groupMember.domain.GroupMemberException;
import com.example.capstonedesign20252.groupMember.repository.GroupMemberRepository;
import com.example.capstonedesign20252.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GroupMemberService {

  private final GroupRepository groupRepository;
  private final GroupMemberRepository groupMemberRepository;

  public void validateGroupLeader(Long groupId, Long userId) {
    Group group = groupRepository.findById(groupId)
                                 .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

    // Group의 생성자(user_id)와 요청자가 같은지 확인
    if (!group.getUser().getId().equals(userId)) {
      throw new GroupMemberException(GroupMemberErrorCode.NOT_GROUP_ADMIN);
    }
  }

  @Transactional
  public int addMembersFromExcel(Long groupId, List<MemberDataDto> memberDataList) {
    Group group = groupRepository.findById(groupId)
                                 .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

    int addedCount = 0;

    for (MemberDataDto data : memberDataList) {
      try {
        // 멤버 중복 확인.
        boolean alreadyExists = false;

        if (data.email() != null && !data.email().isEmpty()) {
          alreadyExists = groupMemberRepository.existsByGroupIdAndEmail(groupId, data.email());
        }

        if (!alreadyExists && data.phone() != null && !data.phone().isEmpty()) {
          alreadyExists = groupMemberRepository.existsByGroupIdAndPhone(groupId, data.phone());
        }

        if (alreadyExists) {
          log.warn("이미 그룹에 존재하는 멤버입니다: {} ({})", data.name(), data.email());
          continue;
        }

        GroupMember member = GroupMember.builder()
                                        .group(group)
                                        .name(data.name())
                                        .email(data.email())
                                        .phone(data.phone())
                                        .isAdmin(false)
                                        .build();

        groupMemberRepository.save(member);
        addedCount++;
        log.debug("멤버 추가: {} ({})", data.name(), data.email());

      } catch (Exception e) {
        log.error("멤버 추가 실패: {} - {}", data.name(), e.getMessage());
      }
    }

    log.info("그룹 {} 멤버 추가 완료: {}명", groupId, addedCount);
    return addedCount;
  }

  public List<GroupMember> getGroupMembers(Long groupId) {
    return groupMemberRepository.findByGroupId(groupId);
  }

  @Transactional
  public void removeMember(Long groupId, Long memberId, Long requesterId) {
    validateGroupLeader(groupId, requesterId);

    GroupMember member = groupMemberRepository
        .findById(memberId)
        .orElseThrow(() -> new GroupMemberException(GroupMemberErrorCode.MEMBER_NOT_FOUND));

    if (!member.getGroup().getId().equals(groupId)) {
      throw new GroupMemberException(GroupMemberErrorCode.MEMBER_NOT_FOUND);
    }

    if (member.getIsAdmin()) {
      throw new GroupMemberException(GroupMemberErrorCode.NOT_DELETE_ADMIN);
    }

    groupMemberRepository.delete(member);
  }
}
