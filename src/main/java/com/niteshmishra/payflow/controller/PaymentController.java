package com.niteshmishra.payflow.controller;

import com.niteshmishra.payflow.config.MerchantPrincipal;
import com.niteshmishra.payflow.dto.PaymentRequestDto;
import com.niteshmishra.payflow.dto.PaymentResponseDto;
import com.niteshmishra.payflow.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(
            @Valid @RequestBody PaymentRequestDto request,
            @AuthenticationPrincipal MerchantPrincipal principal) {

        PaymentResponseDto response = paymentService.createPayment(request, principal.getMerchantId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}