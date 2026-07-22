package com.niteshmishra.payflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("PayFlow is alive");
    }

    @PostMapping
    public ResponseEntity<String> createPayment(@RequestBody String rawBody) {
        return ResponseEntity.ok("Received payment request: " + rawBody);
    }
}