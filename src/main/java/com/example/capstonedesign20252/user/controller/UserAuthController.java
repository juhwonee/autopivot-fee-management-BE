package com.example.capstonedesign20252.user.controller;

import com.example.capstonedesign20252.user.dto.UserInfoResponseDto;
import com.example.capstonedesign20252.user.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {

  private final UserService userService;

  @GetMapping("/me/{userId}")
  public ResponseEntity<UserInfoResponseDto> getUserInfo(@PathVariable Long userId){
    return ResponseEntity.ok(userService.getUserInfo(userId));
  }
}
