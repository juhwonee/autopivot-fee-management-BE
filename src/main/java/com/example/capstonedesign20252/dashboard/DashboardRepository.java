package com.example.capstonedesign20252.dashboard;

import com.example.capstonedesign20252.dashboard.domain.Dashboard;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Long> {
  Optional<Dashboard> findByGroupId(Long groupId);
}
