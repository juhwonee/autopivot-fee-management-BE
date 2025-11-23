package com.example.capstonedesign20252.user.service;

import com.example.capstonedesign20252.user.dto.UserInfoResponseDto;

public interface UserService {

  UserInfoResponseDto getUserInfo(Long userId);
}
