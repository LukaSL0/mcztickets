package com.lukasl.payments.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lukasl.payments.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByOrderId(Long orderId);

    List<Payment> findByOrderId(Long orderId);

}
