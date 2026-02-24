package com.mcztickets.payments.dto.external;

import java.math.BigDecimal;

public record OrderDto(
    Long id,
    Long eventId,
    String status,
    BigDecimal amount
) {}