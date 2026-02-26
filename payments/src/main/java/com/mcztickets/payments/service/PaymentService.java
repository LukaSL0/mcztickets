package com.mcztickets.payments.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mcztickets.payments.dto.CreatePaymentDto;
import com.mcztickets.payments.dto.response.PaymentResponseDto;
import com.mcztickets.payments.entity.Payment;
import com.mcztickets.payments.enums.PaymentStatus;
import com.mcztickets.payments.exception.ConflictException;
import com.mcztickets.payments.exception.ResourceNotFoundException;
import com.mcztickets.payments.kafka.consumer.PaymentKafkaConsumer;
import com.mcztickets.payments.kafka.event.GetOrderRequestEvent;
import com.mcztickets.payments.kafka.event.OrderResponseEvent;
import com.mcztickets.payments.kafka.event.UpdateOrderStatusEvent;
import com.mcztickets.payments.kafka.producer.PaymentKafkaProducer;
import com.mcztickets.payments.repository.PaymentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentKafkaProducer kafkaProducer;
    private final PaymentKafkaConsumer kafkaConsumer;

    public List<PaymentResponseDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(PaymentResponseDto::from)
                .toList();
    }

    public PaymentResponseDto getPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return PaymentResponseDto.from(payment);
    }

    @Transactional
    public PaymentResponseDto createPayment(CreatePaymentDto dto) {
        if (paymentRepository.existsByOrderId(dto.orderId())) {
            throw new ConflictException("Payment already exists for order: " + dto.orderId());
        }

        String correlationId = UUID.randomUUID().toString();
        GetOrderRequestEvent request = new GetOrderRequestEvent(correlationId, dto.orderId());
        kafkaConsumer.registerOrderRequest(correlationId);
        kafkaProducer.sendGetOrderRequest(request);
        OrderResponseEvent orderResponse = kafkaConsumer.waitForOrderResponse(correlationId, 10);

        validatePaymentCanBeCreated(orderResponse);

        Payment payment = Payment.builder()
                .orderId(dto.orderId())
                .userId(dto.userId())
                .amount(orderResponse.amount())
                .paymentMethod(dto.paymentMethod())
                .transactionId("TX-" + System.currentTimeMillis())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return PaymentResponseDto.from(savedPayment);
    }

    @Transactional
    public PaymentResponseDto processPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        validatePaymentCanBeProcessed(payment);

        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);

        boolean paymentApproved = mockPaymentProcessing();

        PaymentStatus newStatus = paymentApproved ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;
        payment.setStatus(newStatus);
        Payment savedPayment = paymentRepository.save(payment);

        updateOrderBasedOnPaymentStatus(savedPayment);
        return PaymentResponseDto.from(savedPayment);
    }

    public void validatePaymentCanBeCreated(OrderResponseEvent order) {
        if (order.status().equals("CANCELLED")) {
            throw new ConflictException("Order already cancelled");
        }
        if (order.status().equals("COMPLETED")) {
            throw new ConflictException("Order already completed");
        }
    }

    private void validatePaymentCanBeProcessed(Payment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new ConflictException("Payment cannot be processed with status: " + payment.getStatus());
        }
    }

    private void updateOrderBasedOnPaymentStatus(Payment payment) {
        String orderStatus = payment.getStatus() == PaymentStatus.COMPLETED ? "COMPLETED" : "CANCELLED";
        sendUpdateOrderStatus(payment.getOrderId(), orderStatus);
    }

    private boolean mockPaymentProcessing() {
        return Math.random() > 0.2;
    }

    private void sendUpdateOrderStatus(Long orderId, String status) {
        UpdateOrderStatusEvent event = new UpdateOrderStatusEvent(orderId, status);
        kafkaProducer.sendUpdateOrderStatus(event);
    }
}
