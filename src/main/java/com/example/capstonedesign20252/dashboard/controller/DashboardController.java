package com.example.capstonedesign20252.dashboard.controller;

import com.example.capstonedesign20252.dashboard.dto.DashboardResponseDto;
import com.example.capstonedesign20252.dashboard.service.DashboardService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Getter
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class DashboardController {

  private final DashboardService dashBoardService;

  @GetMapping("/{groupId}/dashboard")
  public ResponseEntity<DashboardResponseDto> getDashBoard(@PathVariable Long groupId){
    return ResponseEntity.ok(dashBoardService.getDashBoard(groupId));
  }
}
