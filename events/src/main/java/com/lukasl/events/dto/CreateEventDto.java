package com.lukasl.events.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEventDto(
    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Description is required")
    String description,
    
    @NotNull(message = "Event date is required")
    LocalDateTime eventDate,
    
    @NotBlank(message = "Location is required")
    String location,

    @NotNull(message = "Total tickets is required")
    Integer totalTickets,
    
    Integer availableTickets,

    @NotNull(message = "Base price is required")
    BigDecimal basePrice
) {}
