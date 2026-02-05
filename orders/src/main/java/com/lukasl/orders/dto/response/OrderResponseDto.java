package com.lukasl.orders.dto.response;

import java.math.BigDecimal;

import com.lukasl.orders.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private Long id;
    private Long eventId;
    private OrderStatus status;
    private BigDecimal amount;
    
}
