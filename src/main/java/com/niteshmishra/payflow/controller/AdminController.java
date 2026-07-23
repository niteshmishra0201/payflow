package com.niteshmishra.payflow.controller;

import com.niteshmishra.payflow.dto.PaymentResponseDto;
import com.niteshmishra.payflow.mapper.TransactionMapper;
import com.niteshmishra.payflow.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public AdminController(TransactionRepository transactionRepository,
                           TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<PaymentResponseDto>> getAllTransactions() {
        List<PaymentResponseDto> transactions = transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(transactions);
    }
}