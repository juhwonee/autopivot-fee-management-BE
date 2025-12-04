package com.example.capstonedesign20252.payment.repository;

import com.example.capstonedesign20252.groupMember.domain.GroupMember;
import com.example.capstonedesign20252.payment.domain.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

  /**
   * 특정 그룹의 모든 결제 정보
   */
  List<Payment> findByGroupId(Long groupId);

  /**
   * 특정 그룹의 PENDING 상태 결제 건들
   */
  @Query("SELECT p FROM Payment p " +
      "WHERE p.group.id = :groupId " +
      "AND p.status = 'PENDING' " +
      "ORDER BY p.createdAt ASC")
  List<Payment> findPendingPaymentsByGroup(@Param("groupId") Long groupId);

  /**
   * 특정 그룹의 PENDING 상태인 회원들 (미납 회원)
   * 🔥 수정: SELECT g → SELECT p.groupMember
   */
  @Query("SELECT p.groupMember FROM Payment p " +
      "WHERE p.group.id = :groupId " +
      "AND p.status = 'PENDING' " +
      "ORDER BY p.createdAt ASC")
  List<GroupMember> findPendingGroupMemberByGroup(@Param("groupId") Long groupId);

  // 그룹 + 기간으로 조회
  @Query("SELECT p FROM Payment p WHERE p.group.id = :groupId AND p.paymentPeriod = :period")
  List<Payment> findByGroupIdAndPaymentPeriod(@Param("groupId") Long groupId,
      @Param("period") String period);

  // 그룹 + 기간 + 상태로 조회
  @Query("SELECT p FROM Payment p WHERE p.group.id = :groupId AND p.paymentPeriod = :period AND p.status = :status")
  List<Payment> findByGroupIdAndPaymentPeriodAndStatus(@Param("groupId") Long groupId,
      @Param("period") String period,
      @Param("status") String status);

  // 멤버 + 기간 + 상태로 조회 (입금 매칭용)
  @Query("SELECT p FROM Payment p WHERE p.groupMember.id = :memberId AND p.paymentPeriod = :period AND p.status = :status")
  Optional<Payment> findByGroupMemberIdAndPaymentPeriodAndStatus(@Param("memberId") Long memberId,
      @Param("period") String period,
      @Param("status") String status);

  @Modifying
  @Query("DELETE FROM Payment p WHERE p.groupMember.id = :memberId")
  void deleteAllByGroupMemberId(@Param("memberId") Long memberId);


  long countByGroupMemberId(Long groupMemberId);
}
