package com.example.capstonedesign20252.payment.repository;

import com.example.capstonedesign20252.payment.domain.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
  // 특정 그룹의 모든 결제 정보
  List<Payment> findByGroupId(Long groupId);

  // 특정 그룹의 PENDING 상태 결제 건들
  @Query("SELECT p FROM Payment p " +
      "WHERE p.group.id = :groupId " +
      "AND p.status = 'PENDING' " +
      "ORDER BY p.createdAt ASC")
  List<Payment> findPendingPaymentsByGroup(@Param("groupId") Long groupId);
}
