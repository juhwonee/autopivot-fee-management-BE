package com.example.capstonedesign20252.user.service;

import com.example.capstonedesign20252.user.dto.UserInfoResponseDto;
import com.example.capstonedesign20252.user.exception.UserErrorCode;
import com.example.capstonedesign20252.user.exception.UserException;
import com.example.capstonedesign20252.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public UserInfoResponseDto getUserInfo(Long userId) {
    return UserInfoResponseDto.from(userRepository.findById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND)));
  }
}
