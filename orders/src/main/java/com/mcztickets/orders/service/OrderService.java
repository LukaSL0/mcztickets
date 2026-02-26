package com.mcztickets.orders.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mcztickets.orders.dto.CreateOrderDto;
import com.mcztickets.orders.dto.UpdateOrderStatusDto;
import com.mcztickets.orders.dto.response.OrderResponseDto;
import com.mcztickets.orders.entity.Order;
import com.mcztickets.orders.enums.OrderStatus;
import com.mcztickets.orders.exception.ResourceNotFoundException;
import com.mcztickets.orders.kafka.consumer.OrderKafkaConsumer;
import com.mcztickets.orders.kafka.event.EventResponseEvent;
import com.mcztickets.orders.kafka.event.GetEventRequestEvent;
import com.mcztickets.orders.kafka.event.ReserveTicketsEvent;
import com.mcztickets.orders.kafka.producer.OrderKafkaProducer;
import com.mcztickets.orders.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderKafkaProducer kafkaProducer;
    private final OrderKafkaConsumer kafkaConsumer;

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
        String correlationId = UUID.randomUUID().toString();
        GetEventRequestEvent request = new GetEventRequestEvent(correlationId, dto.eventId());
        kafkaConsumer.registerEventRequest(correlationId);
        kafkaProducer.sendGetEventRequest(request);
        EventResponseEvent eventResponse = kafkaConsumer.waitForEventResponse(correlationId, 10);

        BigDecimal totalPrice = eventResponse.basePrice().multiply(BigDecimal.valueOf(dto.tickets()));

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
    public OrderResponseDto updateOrderStatus(Long orderId, UpdateOrderStatusDto dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = dto.status();

        order.setStatus(newStatus);

        if (oldStatus != OrderStatus.COMPLETED && newStatus == OrderStatus.COMPLETED) {
            ReserveTicketsEvent event = new ReserveTicketsEvent(order.getEventId(), order.getTickets());
            kafkaProducer.sendReserveTickets(event);
        }

        Order updatedOrder = orderRepository.save(order);
        return OrderResponseDto.from(updatedOrder);
    }
}
