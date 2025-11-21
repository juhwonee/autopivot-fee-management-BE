package com.example.capstonedesign20252.dashboard.controller;

import com.example.capstonedesign20252.dashboard.dto.DashboardResponseDto;
import com.example.capstonedesign20252.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/{groupId}/dashboard")
  public ResponseEntity<DashboardResponseDto> getDashboard(@PathVariable Long groupId) {
    log.info("대시보드 조회 요청 - groupId: {}", groupId);
    return ResponseEntity.ok(dashboardService.getDashBoard(groupId));
  }

  @PostMapping("/{groupId}/dashboard/refresh")
  public ResponseEntity<Void> refreshDashboard(@PathVariable Long groupId) {
    log.info("대시보드 캐시 갱신 요청 - groupId: {}", groupId);
    dashboardService.evictDashboardCache(groupId);
    return ResponseEntity.ok().build();
  }
}