package com.example.capstonedesign20252.groupMember.repository;

import com.example.capstonedesign20252.groupMember.domain.GroupMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
  List<GroupMember> findByUserId(Long userId);

  boolean existsByGroupIdAndUserId(Long groupId, Long userId);
}
