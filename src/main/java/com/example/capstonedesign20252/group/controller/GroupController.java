package com.example.capstonedesign20252.group.controller;

import com.example.capstonedesign20252.group.dto.GroupResponseDto;
import com.example.capstonedesign20252.group.dto.UpdateRequestGroupDto;
import com.example.capstonedesign20252.group.dto.createGroupRequestDto;
import com.example.capstonedesign20252.group.service.GroupService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

  private final GroupService groupService;

  /**
   * 그룹 생성 (멤버 없이)
   * POST /api/groups
   *
   * 변경사항: memberFile 파라미터 제거, JSON Body로만 그룹 생성
   */
  @PostMapping
  public ResponseEntity<GroupResponseDto> createGroup(
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestBody createGroupRequestDto dto
  ) {
    Long userId = Long.parseLong(userDetails.getUsername());
    log.info("그룹 생성 요청 - User ID: {}, 그룹명: {}", userId, dto.groupName());

    GroupResponseDto response = groupService.createGroup(userId, dto);

    log.info("그룹 생성 완료 - Group ID: {}", response.groupId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/my")
  public ResponseEntity<List<GroupResponseDto>> getMyGroups(
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    Long userId = Long.parseLong(userDetails.getUsername());
    return ResponseEntity.ok(groupService.getUserGroups(userId));
  }

  @GetMapping
  public ResponseEntity<List<GroupResponseDto>> getAllGroups() {
    return ResponseEntity.ok(groupService.getAllGroups());
  }

  @GetMapping("/{groupId:\\d+}")
  public ResponseEntity<GroupResponseDto> getGroup(
      @PathVariable Long groupId
  ) {
    return ResponseEntity.ok(groupService.getGroup(groupId));
  }

  @PatchMapping("/{groupId:\\d+}")
  public ResponseEntity<GroupResponseDto> updateGroup(
      @PathVariable Long groupId,
      @RequestBody UpdateRequestGroupDto updateRequestGroupDto
  ){
    return ResponseEntity.ok(groupService.updateGroup(groupId, updateRequestGroupDto));
  }

  @DeleteMapping("/{groupId:\\d+}")
  public ResponseEntity<Void> deleteGroup(
      @PathVariable Long groupId
  ) {
    groupService.deleteGroup(groupId);
    return ResponseEntity.noContent().build();
  }
}
