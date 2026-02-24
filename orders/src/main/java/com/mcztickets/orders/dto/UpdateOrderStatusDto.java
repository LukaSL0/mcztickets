package com.mcztickets.orders.dto;

import com.mcztickets.orders.enums.OrderStatus;

public record UpdateOrderStatusDto(
    OrderStatus status
) {}
