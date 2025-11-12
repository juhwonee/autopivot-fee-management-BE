package com.example.capstonedesign20252.user.repository;

import com.example.capstonedesign20252.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  //일반 유저용
  Optional<User> findByEmail(String email);
  boolean existsByEmail(String email);

  //카카오용
  Optional<User> findByKakaoId(String kakaoId);
  boolean existsByKakaoId(String kakaoId);
}
