package com.example.capstonedesign20252.payment.repository;

import com.example.capstonedesign20252.payment.domain.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {

}
