package com.example.capstonedesign20252.user.service;

import com.example.capstonedesign20252.user.dto.LoginRequestDto;
import com.example.capstonedesign20252.user.dto.LoginResponseDto;
import com.example.capstonedesign20252.user.dto.SignUpRequestDto;
import com.example.capstonedesign20252.user.dto.SignUpResponseDto;

public interface UserService {

  SignUpResponseDto signUp(SignUpRequestDto dto);
  LoginResponseDto login(LoginRequestDto dto);
}
