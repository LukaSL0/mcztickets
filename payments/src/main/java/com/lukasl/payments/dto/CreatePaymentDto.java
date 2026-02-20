package com.lukasl.payments.dto;

import java.util.UUID;

import com.lukasl.payments.enums.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreatePaymentDto(

    @NotNull(message = "Order ID is required")
    Long orderId,

    @NotNull(message = "User ID is required")
    UUID userId,

    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod
) {}
