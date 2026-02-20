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
import com.lukasl.orders.dto.response.OrderResponseDto;
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

    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponseDto::from)
                .toList();
    }

    public OrderResponseDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return OrderResponseDto.from(order);
    }

    @Transactional
    public OrderResponseDto createOrder(CreateOrderDto dto) {
        EventDto eventDto = eventClient.getEventById(dto.eventId());

        BigDecimal totalPrice = eventDto.basePrice().multiply(BigDecimal.valueOf(dto.tickets()));

        Order order = Order.builder()
            .userId(dto.userId())
            .eventId(dto.eventId())
            .tickets(dto.tickets())
            .amount(totalPrice)
            .build();

        Order savedOrder = orderRepository.save(order);

        return OrderResponseDto.from(savedOrder);
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(
        Long orderId,
        UpdateOrderStatusDto dto
    ) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = dto.status();

        order.setStatus(newStatus);

        if (oldStatus != OrderStatus.COMPLETED && newStatus == OrderStatus.COMPLETED) {
            UpdateEventDto updateEventDto = UpdateEventDto.builder()
                .tickets(order.getTickets())
                .build();
            
            eventClient.reserveTickets(order.getEventId(), updateEventDto);
        }

        Order updatedOrder = orderRepository.save(order);
        
        return OrderResponseDto.from(updatedOrder);
    }
}
