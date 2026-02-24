package com.mcztickets.orders.dto.response;

import java.math.BigDecimal;

import com.mcztickets.orders.entity.Order;
import com.mcztickets.orders.enums.OrderStatus;

import lombok.Builder;

@Builder
public record OrderResponseDto(
    Long id,
    Long eventId,
    OrderStatus status,
    BigDecimal amount
) {
    public static OrderResponseDto from(Order order) {
        return OrderResponseDto.builder()
                .id(order.getId())
                .eventId(order.getEventId())
                .status(order.getStatus())
                .amount(order.getAmount())
                .build();
    }
}
