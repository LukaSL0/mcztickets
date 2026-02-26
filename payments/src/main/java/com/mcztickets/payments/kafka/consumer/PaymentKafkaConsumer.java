package com.mcztickets.payments.kafka.consumer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.mcztickets.payments.kafka.event.OrderResponseEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentKafkaConsumer {

    private final ConcurrentHashMap<String, CompletableFuture<OrderResponseEvent>> orderPendingRequests = new ConcurrentHashMap<>();

    public CompletableFuture<OrderResponseEvent> registerOrderRequest(String correlationId) {
        CompletableFuture<OrderResponseEvent> future = new CompletableFuture<>();
        orderPendingRequests.put(correlationId, future);
        return future;
    }

    @KafkaListener(topics = "orders.get.response", groupId = "payments-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeOrderResponse(OrderResponseEvent event) {
        log.info("Received OrderResponseEvent for correlationId={}", event.correlationId());
        CompletableFuture<OrderResponseEvent> future = orderPendingRequests.remove(event.correlationId());
        if (future != null) {
            future.complete(event);
        } else {
            log.warn("No pending request found for correlationId={}", event.correlationId());
        }
    }

    public OrderResponseEvent waitForOrderResponse(String correlationId, long timeoutSeconds) {
        try {
            CompletableFuture<OrderResponseEvent> future = orderPendingRequests.get(correlationId);
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            orderPendingRequests.remove(correlationId);
            throw new RuntimeException("Timeout waiting for order data from orders-service", e);
        }
    }
}
