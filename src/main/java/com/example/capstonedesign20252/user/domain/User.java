package com.example.capstonedesign20252.user.domain;

import com.example.capstonedesign20252.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users") // 예약어
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(name = "kakao_id", unique = true)
  private String kakaoId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LoginType loginType;

  @Builder
  public User(String name, String email, String password, String kakaoId, LoginType loginType){
    this.name = name;
    this.email = email;
    this.password = password;
    this.kakaoId = kakaoId;
    this.loginType = loginType;
  }

  public static User createGeneralUser(String name, String email, String encodedPassword){
    return User.builder()
        .name(name)
        .email(email)
        .password(encodedPassword)
        .loginType(LoginType.GENERAL)
        .build();
  }

  public static User createKakaoUser(String name, String email, String kakaoId, LoginType loginType){
    return User.builder()
        .name(name)
        .email(email)
        .kakaoId(kakaoId)
        .loginType(LoginType.KAKAO)
        .build();
  }
}
