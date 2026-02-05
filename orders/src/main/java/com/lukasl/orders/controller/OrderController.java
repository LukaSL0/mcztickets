package com.lukasl.orders.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lukasl.orders.dto.CreateOrderDto;
import com.lukasl.orders.dto.UpdateOrderStatusDto;
import com.lukasl.orders.dto.response.OrderResponseDto;
import com.lukasl.orders.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping()
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        List<OrderResponseDto> allOders = orderService.getAllOrders();
        return ResponseEntity.ok(allOders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long orderId) {
        OrderResponseDto order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @PostMapping()
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody CreateOrderDto dto) {
        OrderResponseDto orderResponse = orderService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderResponse);
    }   

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
        @PathVariable Long orderId,
        @Valid @RequestBody UpdateOrderStatusDto dto
    ) {
        OrderResponseDto updatedOrder = orderService.updateOrderStatus(orderId, dto);
        return ResponseEntity.ok(updatedOrder);
    }
}
