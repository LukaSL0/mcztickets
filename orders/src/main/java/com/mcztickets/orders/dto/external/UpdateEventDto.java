package com.mcztickets.orders.dto.external;

import lombok.Builder;

@Builder
public record UpdateEventDto(
    Integer tickets
) {}
