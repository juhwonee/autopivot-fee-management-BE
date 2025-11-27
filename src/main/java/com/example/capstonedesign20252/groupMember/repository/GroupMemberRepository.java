package com.example.capstonedesign20252.groupMember.repository;

import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.groupMember.domain.GroupMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

  List<GroupMember> findByGroupId(Long groupId);

  boolean existsByGroupIdAndEmail(Long groupId, String email);
  boolean existsByGroupIdAndPhone(Long groupId, String phone);
  boolean existsByGroupIdAndEmailAndIdNot(Long groupId, String email, Long id);
  boolean existsByGroupIdAndPhoneAndIdNot(Long groupId, String phone, Long id);
}