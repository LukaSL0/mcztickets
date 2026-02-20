package com.lukasl.payments.dto.external;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateOrderStatusDto(

    @NotNull(message = "Status is required")
    String status
) {}
