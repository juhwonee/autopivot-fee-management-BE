package com.example.capstonedesign20252.dashboard.service;

import com.example.capstonedesign20252.dashboard.dto.DashboardResponseDto;

public interface DashboardService {
  DashboardResponseDto getDashBoard(Long groupId);
  void evictDashboardCache(Long groupId);
}