package com.example.capstonedesign20252.paymentCycle.repository;

import com.example.capstonedesign20252.paymentCycle.domain.PaymentCycle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentCycleRepository extends JpaRepository<PaymentCycle, Long> {

  @Query("SELECT pc FROM PaymentCycle pc WHERE pc.group.id = :groupId AND pc.status = :status")
  Optional<PaymentCycle> findByGroupIdAndStatus(@Param("groupId") Long groupId,
                                                 @Param("status") String status);

  @Query("SELECT pc FROM PaymentCycle pc WHERE pc.group.id = :groupId AND pc.period = :period")
  Optional<PaymentCycle> findByGroupIdAndPeriod(@Param("groupId") Long groupId,
                                                 @Param("period") String period);

  boolean existsByGroupIdAndStatus(Long groupId, String status);

  List<PaymentCycle> findByGroupIdOrderByCreatedAtDesc(Long groupId);

  @Query("SELECT pc FROM PaymentCycle pc WHERE pc.group.id = :groupId ORDER BY pc.createdAt DESC")
  List<PaymentCycle> findRecentByGroupId(@Param("groupId") Long groupId);
}
