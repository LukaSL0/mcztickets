package com.mcztickets.payments.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.mcztickets.payments.kafka.event.GetOrderRequestEvent;
import com.mcztickets.payments.kafka.event.UpdateOrderStatusEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendGetOrderRequest(GetOrderRequestEvent event) {
        log.info("Sending GetOrderRequest for orderId={}, correlationId={}",
                event.orderId(), event.correlationId());
        kafkaTemplate.send("orders.get.request", event.correlationId(), event);
    }

    public void sendUpdateOrderStatus(UpdateOrderStatusEvent event) {
        log.info("Sending UpdateOrderStatus for orderId={}, status={}",
                event.orderId(), event.status());
        kafkaTemplate.send("orders.update.status", String.valueOf(event.orderId()), event);
    }
}
