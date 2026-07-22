package com.niteshmishra.payflow.mapper;

import com.niteshmishra.payflow.dto.PaymentResponseDto;
import com.niteshmishra.payflow.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public PaymentResponseDto toResponseDto(Transaction transaction) {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(transaction.getId());
        dto.setMerchantId(transaction.getMerchant().getId());
        dto.setAmount(transaction.getAmount());
        dto.setCurrency(transaction.getCurrency());
        dto.setStatus(transaction.getStatus());
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }
}