package com.mcztickets.orders.kafka.consumer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.mcztickets.orders.dto.UpdateOrderStatusDto;
import com.mcztickets.orders.enums.OrderStatus;
import com.mcztickets.orders.kafka.event.EventResponseEvent;
import com.mcztickets.orders.kafka.event.GetOrderRequestEvent;
import com.mcztickets.orders.kafka.event.OrderResponseEvent;
import com.mcztickets.orders.kafka.event.UpdateOrderStatusEvent;
import com.mcztickets.orders.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderKafkaConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderService orderService;

    @Autowired
    public OrderKafkaConsumer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Lazy OrderService orderService) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderService = orderService;
    }

    private final ConcurrentHashMap<String, CompletableFuture<EventResponseEvent>> eventPendingRequests = new ConcurrentHashMap<>();

    public CompletableFuture<EventResponseEvent> registerEventRequest(String correlationId) {
        CompletableFuture<EventResponseEvent> future = new CompletableFuture<>();
        eventPendingRequests.put(correlationId, future);
        return future;
    }

    @KafkaListener(topics = "events.get.response", groupId = "orders-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeEventResponse(EventResponseEvent event) {
        log.info("Received EventResponseEvent for correlationId={}", event.correlationId());
        CompletableFuture<EventResponseEvent> future = eventPendingRequests.remove(event.correlationId());
        if (future != null) {
            future.complete(event);
        } else {
            log.warn("No pending request found for correlationId={}", event.correlationId());
        }
    }

    @KafkaListener(topics = "orders.get.request", groupId = "orders-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeGetOrderRequest(GetOrderRequestEvent event) {
        log.info("Received GetOrderRequest for orderId={}, correlationId={}", event.orderId(), event.correlationId());
        try {
            var orderDto = orderService.getOrderById(event.orderId());
            OrderResponseEvent response = new OrderResponseEvent(
                    event.correlationId(),
                    orderDto.id(),
                    orderDto.eventId(),
                    orderDto.status().name(),
                    orderDto.amount());
            kafkaTemplate.send("orders.get.response", event.correlationId(), response);
        } catch (Exception e) {
            log.error("Failed to process GetOrderRequest for orderId={}: {}", event.orderId(), e.getMessage());
        }
    }

    @KafkaListener(topics = "orders.update.status", groupId = "orders-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUpdateOrderStatus(UpdateOrderStatusEvent event) {
        log.info("Received UpdateOrderStatusEvent for orderId={}, status={}", event.orderId(), event.status());
        try {
            UpdateOrderStatusDto dto = new UpdateOrderStatusDto(OrderStatus.valueOf(event.status()));
            orderService.updateOrderStatus(event.orderId(), dto);
        } catch (Exception e) {
            log.error("Failed to update order status for orderId={}: {}", event.orderId(), e.getMessage());
        }
    }

    public EventResponseEvent waitForEventResponse(String correlationId, long timeoutSeconds) {
        try {
            CompletableFuture<EventResponseEvent> future = eventPendingRequests.get(correlationId);
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            eventPendingRequests.remove(correlationId);
            throw new RuntimeException("Timeout waiting for event data from events-service", e);
        }
    }
}
