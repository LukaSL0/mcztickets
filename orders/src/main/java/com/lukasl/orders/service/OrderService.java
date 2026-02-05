package com.lukasl.orders.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lukasl.orders.client.EventClient;
import com.lukasl.orders.dto.CreateOrderDto;
import com.lukasl.orders.dto.UpdateOrderStatusDto;
import com.lukasl.orders.dto.external.EventDto;
import com.lukasl.orders.dto.external.UpdateEventDto;
import com.lukasl.orders.dto.response.OrderDto;
import com.lukasl.orders.entity.Order;
import com.lukasl.orders.enums.OrderStatus;
import com.lukasl.orders.exception.ResourceNotFoundException;
import com.lukasl.orders.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final EventClient eventClient;

    public List<OrderDto> getAllOrders() {
        return toListResponseDto(orderRepository.findAll());
    }

    public OrderDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return OrderDto.builder()
            .id(order.getId())
            .eventId(order.getEventId())
            .status(order.getStatus())
            .amount(order.getAmount())
            .build();
    }

    @Transactional
    public OrderDto createOrder(CreateOrderDto dto) {
        EventDto eventDto = eventClient.getEventById(dto.getEventId());

        BigDecimal totalPrice = eventDto.getBasePrice().multiply(BigDecimal.valueOf(dto.getTickets()));

        Order order = Order.builder()
            .userId(dto.getUserId())
            .eventId(dto.getEventId())
            .tickets(dto.getTickets())
            .amount(totalPrice)
            .build();

        Order savedOrder = orderRepository.save(order);

        return toResponseDto(savedOrder);
    }

    @Transactional
    public OrderDto updateOrderStatus(
        Long orderId,
        UpdateOrderStatusDto dto
    ) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = dto.getStatus();

        order.setStatus(newStatus);

        if (oldStatus != OrderStatus.COMPLETED && newStatus == OrderStatus.COMPLETED) {
            UpdateEventDto updateEventDto = UpdateEventDto.builder()
                .tickets(order.getTickets())
                .build();
            
            eventClient.reserveTickets(order.getEventId(), updateEventDto);
        }

        Order updatedOrder = orderRepository.save(order);
        
        return toResponseDto(updatedOrder);
    }

    // ========== HELPERS ==========

    private List<OrderDto> toListResponseDto(List<Order> orders) {
        return orders.stream()
        .map(this::toResponseDto)
        .toList();
    }

    private OrderDto toResponseDto(Order order) {
        return OrderDto.builder()
            .id(order.getId())
            .eventId(order.getEventId())
            .status(order.getStatus())
            .amount(order.getAmount())
            .build();
    }

}
