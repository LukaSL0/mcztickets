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
import com.lukasl.payments.dto.response.PaymentResponseDto;
import com.lukasl.payments.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping()
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        List<PaymentResponseDto> allPayments = paymentService.getAllPayments();
        return ResponseEntity.status(HttpStatus.OK).body(allPayments);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getPayment(@PathVariable Long paymentId) {
        PaymentResponseDto payment = paymentService.getPayment(paymentId);
        return ResponseEntity.status(HttpStatus.OK).body(payment);
    }

    @PostMapping()
    public ResponseEntity<PaymentResponseDto> createPayment(@Valid @RequestBody CreatePaymentDto dto) {
        PaymentResponseDto createdPayment = paymentService.createPayment(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(createdPayment);
    }

    @PostMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> processPayment(@PathVariable Long paymentId) {
        PaymentResponseDto processedPayment = paymentService.processPayment(paymentId);
        return ResponseEntity.status(HttpStatus.OK).body(processedPayment);
    }
}
