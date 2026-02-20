package com.lukasl.orders.dto;

import com.lukasl.orders.enums.OrderStatus;

public record UpdateOrderStatusDto(
    OrderStatus status
) {}
