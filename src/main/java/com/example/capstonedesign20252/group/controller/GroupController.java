package com.example.capstonedesign20252.group.controller;

import com.example.capstonedesign20252.group.dto.GroupResponseDto;
import com.example.capstonedesign20252.group.dto.createGroupRequestDto;
import com.example.capstonedesign20252.group.service.GroupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

  private final GroupService groupService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<GroupResponseDto> createGroup(
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestPart("groupData") String groupDataJson,
      @RequestPart(value = "memberFile", required = false) MultipartFile memberFile
  ) throws Exception {
    Long userId = Long.parseLong(userDetails.getUsername());

    // JSON 문자열을 DTO로 파싱
    ObjectMapper mapper = new ObjectMapper();
    createGroupRequestDto dto = mapper.readValue(groupDataJson, createGroupRequestDto.class);

    return ResponseEntity.status(HttpStatus.CREATED)
                         .body(groupService.createGroup(userId, dto, memberFile));
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

  @DeleteMapping("/{groupId:\\d+}")
  public ResponseEntity<Void> deleteGroup(
      @PathVariable Long groupId
  ) {
    groupService.deleteGroup(groupId);
    return ResponseEntity.noContent().build();
  }
}