package com.example.capstonedesign20252.group.repository;

import com.example.capstonedesign20252.group.domain.Group;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
  List<Group> findByUserId(Long userId);
  Optional<Group> findByAccountName(String accountName);

  @Query("SELECT g FROM Group g WHERE " +
         "g.accountName = :accountName OR" +
         ":accountName LIKE CONCAT('%', g.groupName, '%')")
  List<Group> findByAccountNameOrContainsGroupName(@Param("accountName") String accountName);
}
