package com.example.capstonedesign20252.group.controller;

import com.example.capstonedesign20252.group.dto.GroupResponseDto;
import com.example.capstonedesign20252.group.dto.createGroupRequestDto;
import com.example.capstonedesign20252.group.service.GroupService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

  private final GroupService groupService;

  @PostMapping("/{userId}")
  public ResponseEntity<GroupResponseDto> createGroup(
      @PathVariable Long userId,
      @RequestBody createGroupRequestDto dto
  ) {
    return ResponseEntity.status(HttpStatus.CREATED).body(groupService.createGroup(userId, dto));
  }

  @GetMapping("/{groupId}")
  public ResponseEntity<GroupResponseDto> getGroup(@PathVariable Long groupId){
    return ResponseEntity.ok(groupService.getGroup(groupId));
  }

  @GetMapping
  public ResponseEntity<List<GroupResponseDto>> getAllGroups(){
    return ResponseEntity.ok(groupService.getAllGroups());
  }

  @DeleteMapping("/{groupId}")
  public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId){
    groupService.deleteGroup(groupId);
    return ResponseEntity.noContent().build();
  }
}
