package com.mcztickets.payments.dto;

import java.util.UUID;

import com.mcztickets.payments.enums.PaymentMethod;

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
