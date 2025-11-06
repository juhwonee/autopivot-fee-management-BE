package com.example.capstonedesign20252.group.repository;

import com.example.capstonedesign20252.group.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

}
