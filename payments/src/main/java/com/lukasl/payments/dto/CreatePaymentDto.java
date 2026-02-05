package com.lukasl.payments.dto;

import java.util.UUID;

import com.lukasl.payments.enums.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentDto {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
