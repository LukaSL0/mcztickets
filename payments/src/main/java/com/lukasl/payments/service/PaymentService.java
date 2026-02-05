package com.lukasl.payments.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lukasl.payments.client.OrderClient;
import com.lukasl.payments.dto.CreatePaymentDto;
import com.lukasl.payments.dto.external.OrderDto;
import com.lukasl.payments.dto.external.UpdateOrderStatusDto;
import com.lukasl.payments.dto.response.PaymentResponseDto;
import com.lukasl.payments.entity.Payment;
import com.lukasl.payments.enums.PaymentStatus;
import com.lukasl.payments.exception.ConflictException;
import com.lukasl.payments.exception.ResourceNotFoundException;
import com.lukasl.payments.repository.PaymentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;

    public List<PaymentResponseDto> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return toListResponseDto(payments);
    }

    public PaymentResponseDto getPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return toResponseDto(payment);
    }

    @Transactional
    public PaymentResponseDto createPayment(CreatePaymentDto dto) {
        if (paymentRepository.existsByOrderId(dto.getOrderId())) {
            throw new ConflictException("Payment already exists for order: " + dto.getOrderId());
        }

        OrderDto order = orderClient.getOrderById(dto.getOrderId());
        validatePaymentCanBeCreated(order);

        Payment payment = Payment.builder()
            .orderId(dto.getOrderId())
            .userId(dto.getUserId())
            .amount(order.getAmount())
            .paymentMethod(dto.getPaymentMethod())
            .transactionId("TX-" + System.currentTimeMillis())
            .build();

        Payment savedPayment = paymentRepository.save(payment);

        return toResponseDto(savedPayment);
    }

    @Transactional
    public PaymentResponseDto processPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        validatePaymentCanBeProcessed(payment);
        
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);
        
        boolean paymentApproved = mockPaymentProcessing();
        
        PaymentStatus newStatus = paymentApproved ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;
        payment.setStatus(newStatus);
        Payment savedPayment = paymentRepository.save(payment);
        
        updateOrderBasedOnPaymentStatus(savedPayment);
        
        return toResponseDto(savedPayment);
    }

    // ========== HELPERS ==========

    public void validatePaymentCanBeCreated(OrderDto order) {
        if (order.getStatus().equals("CANCELLED")) {
            throw new ConflictException("Order already cancelled");
        }
        if (order.getStatus().equals("COMPLETED")) {
            throw new ConflictException("Order already completed");
        }
    }

    private void validatePaymentCanBeProcessed(Payment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new ConflictException(
                "Payment cannot be processed with status: " + payment.getStatus()
            );
        }
    }

    private void updateOrderBasedOnPaymentStatus(Payment payment) {
        String orderStatus = payment.getStatus() == PaymentStatus.COMPLETED 
            ? "COMPLETED" 
            : "CANCELLED";
        updateOrderStatus(payment.getOrderId(), orderStatus);
    }

    private boolean mockPaymentProcessing() {
        return Math.random() > 0.2;
    }

    private void updateOrderStatus(Long orderId, String status) {
        UpdateOrderStatusDto dto = UpdateOrderStatusDto.builder()
            .status(status)
            .build();
        
        orderClient.updateOrderStatus(orderId, dto);
    }
    
    private List<PaymentResponseDto> toListResponseDto(List<Payment> payments) {
        return payments.stream()
        .map(this::toResponseDto)
        .toList();
    }

    private PaymentResponseDto toResponseDto(Payment payment) {
        return PaymentResponseDto.builder()
            .id(payment.getId())
            .orderId(payment.getOrderId())
            .userId(payment.getUserId())
            .amount(payment.getAmount())
            .paymentMethod(payment.getPaymentMethod())
            .status(payment.getStatus())
            .transactionId(payment.getTransactionId())
            .failureReason(payment.getFailureReason())
            .createdAt(payment.getCreatedAt())
            .build();
    }
}
