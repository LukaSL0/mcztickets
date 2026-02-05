package com.lukasl.payments.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lukasl.payments.dto.CreatePaymentDto;
import com.lukasl.payments.dto.response.PaymentDto;
import com.lukasl.payments.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping()
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        List<PaymentDto> allPayments = paymentService.getAllPayments();
        return ResponseEntity.ok(allPayments);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable Long paymentId) {
        PaymentDto payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping()
    public ResponseEntity<PaymentDto> createPayment(@Valid @RequestBody CreatePaymentDto dto) {
        PaymentDto createdPayment = paymentService.createPayment(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(createdPayment);
    }

    @PostMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> processPayment(@PathVariable Long paymentId) {
        PaymentDto processedPayment = paymentService.processPayment(paymentId);
        return ResponseEntity.ok(processedPayment);
    }
}
