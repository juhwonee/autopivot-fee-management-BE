package com.example.capstonedesign20252.paymentLog.repository;

import com.example.capstonedesign20252.paymentLog.domain.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {

}
