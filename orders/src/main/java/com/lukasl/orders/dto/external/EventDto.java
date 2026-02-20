package com.lukasl.orders.dto.external;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventDto(
    Long id,
    String name,
    String description,
    LocalDateTime eventDate,
    String location,
    Integer totalTickets,
    Integer availableTickets,
    BigDecimal basePrice
) {}
