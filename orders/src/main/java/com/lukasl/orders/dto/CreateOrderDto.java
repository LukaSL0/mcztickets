package com.lukasl.orders.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderDto(
    
    @NotNull(message = "User ID is required")
    UUID userId,
    
    @NotNull(message = "Event ID is required")
    Long eventId,
    
    @NotNull(message = "Tickets are required")
    @Positive(message = "Tickets must be positive")
    Integer tickets
) {}
