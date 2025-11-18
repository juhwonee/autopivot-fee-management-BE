package com.example.capstonedesign20252.group.service;

import com.example.capstonedesign20252.excel.dto.MemberDataDto;
import com.example.capstonedesign20252.excel.service.ExcelParserService;
import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.group.dto.GroupResponseDto;
import com.example.capstonedesign20252.group.dto.createGroupRequestDto;
import com.example.capstonedesign20252.group.repository.GroupRepository;
import com.example.capstonedesign20252.groupMember.domain.GroupMember;
import com.example.capstonedesign20252.groupMember.repository.GroupMemberRepository;
import com.example.capstonedesign20252.user.domain.User;
import com.example.capstonedesign20252.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final GroupMemberRepository groupMemberRepository;
  private final ExcelParserService excelParserService;

  public GroupResponseDto createGroup(Long userId, createGroupRequestDto dto, MultipartFile memberFile) {
    User user = userRepository.findById(userId)
                              .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

    Group group = Group.builder()
                       .user(user)
                       .groupName(dto.groupName())
                       .description(dto.description())
                       .groupCategory(dto.groupCategory())
                       .fee(dto.fee())
                       .build();

    Group savedGroup = groupRepository.save(group);
    log.info("그룹 생성 완료: {} (ID: {})", savedGroup.getGroupName(), savedGroup.getId());

    // 2. 그룹 생성자를 관리자로 추가
    GroupMember adminMember = GroupMember.builder()
                                         .group(savedGroup)
                                         .user(user)
                                         .isAdmin(true)
                                         .build();
    groupMemberRepository.save(adminMember);
    log.info("그룹 관리자 추가: {} (User ID: {})", user.getName(), userId);

    // 3. 엑셀 파일이 있으면 멤버 추가
    if (memberFile != null && !memberFile.isEmpty()) {
      try {
        log.info("엑셀 파일 처리 시작: {}", memberFile.getOriginalFilename());

        List<MemberDataDto> members = excelParserService.parseExcelFile(memberFile);
        log.info("엑셀에서 {}명의 멤버 정보를 추출했습니다.", members.size());

        // 4. 추출한 멤버 데이터를 DB에 저장
        int successCount = 0;
        int failCount = 0;

        for (MemberDataDto memberData : members) {
          try {
            // 전화번호로 기존 사용자 찾기
            User memberUser = null;

            if (memberData.phone() != null && !memberData.phone().isEmpty()) {
              memberUser = userRepository.findByPhone(memberData.phone()).orElse(null);
            }

            // 기존 사용자가 없으면 새로 생성
            if (memberUser == null) {
              User newUser = User.builder()
                                 .name(memberData.name())
                                 .phone(memberData.phone())
                                 .email(memberData.email())
                                 .build();
              memberUser = userRepository.save(newUser);
              log.debug("신규 사용자 생성: {}", memberData.name());
            } else {
              log.debug("기존 사용자 발견: {} (ID: {})", memberUser.getName(), memberUser.getId());
            }

            // 이미 그룹 멤버인지 확인
            boolean alreadyMember = groupMemberRepository.existsByGroupIdAndUserId(
                savedGroup.getId(),
                memberUser.getId()
            );

            if (alreadyMember) {
              log.warn("이미 그룹 멤버입니다: {} (User ID: {})", memberUser.getName(), memberUser.getId());
              failCount++;
              continue;
            }

            // 그룹 멤버로 추가
            GroupMember groupMember = GroupMember.builder()
                                                 .group(savedGroup)
                                                 .user(memberUser)
                                                 .isAdmin(false)
                                                 .build();
            groupMemberRepository.save(groupMember);

            successCount++;
            log.debug("그룹 멤버 추가 완료: {}", memberData.name());

          } catch (Exception e) {
            log.error("멤버 추가 실패: {} - {}", memberData.name(), e.getMessage());
            failCount++;
          }
        }

        log.info("멤버 추가 완료: 성공 {}명, 실패 {}명", successCount, failCount);

        log.info("엑셀 파일 처리 완료! (멤버 저장 로직은 추후 구현)");

      } catch (Exception e) {
        log.error("엑셀 파일 처리 중 오류 발생: {}", e.getMessage(), e);
        // 그룹은 생성되었지만 멤버 추가만 실패
        // 원한다면 여기서 throw해서 전체 롤백 가능
      }
    } else {
      log.info("엑셀 파일이 없습니다. 멤버 추가 없이 그룹만 생성됩니다.");
    }

    return GroupResponseDto.from(savedGroup);
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
