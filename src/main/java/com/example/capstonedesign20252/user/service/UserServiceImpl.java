package com.example.capstonedesign20252.user.service;

import com.example.capstonedesign20252.user.domain.User;
import com.example.capstonedesign20252.user.dto.LoginRequestDto;
import com.example.capstonedesign20252.user.dto.LoginResponseDto;
import com.example.capstonedesign20252.user.dto.SignUpRequestDto;
import com.example.capstonedesign20252.user.dto.SignUpResponseDto;
import com.example.capstonedesign20252.user.exception.UserErrorCode;
import com.example.capstonedesign20252.user.exception.UserException;
import com.example.capstonedesign20252.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public SignUpResponseDto signUp(SignUpRequestDto dto) {
    if(userRepository.existsByEmail(dto.email())){
      throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
    }

    String encodedPassword = passwordEncoder.encode(dto.password());
    User saved = userRepository.save(User.createGeneralUser(dto.name(), dto.email(), encodedPassword));
    return SignUpResponseDto.from(saved);
  }

  @Override
  public LoginResponseDto login(LoginRequestDto dto) {

    User user = userRepository.findByEmail(dto.email())
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    if(!passwordEncoder.matches(dto.password(), user.getPassword())){
      throw new UserException(UserErrorCode.INVALID_PASSWORD);
    }

    return LoginResponseDto.from(user);
  }
}
