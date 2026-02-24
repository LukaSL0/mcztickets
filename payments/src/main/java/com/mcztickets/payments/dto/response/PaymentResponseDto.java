package com.mcztickets.payments.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mcztickets.payments.entity.Payment;
import com.mcztickets.payments.enums.PaymentMethod;
import com.mcztickets.payments.enums.PaymentStatus;

import lombok.Builder;

@Builder
public record PaymentResponseDto(
    Long id,
    Long orderId,
    UUID userId,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    PaymentStatus status,
    String transactionId,
    String failureReason,
    LocalDateTime createdAt
) {
    public static PaymentResponseDto from(Payment payment) {
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