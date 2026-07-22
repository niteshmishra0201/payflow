package com.niteshmishra.payflow.dto;

import com.niteshmishra.payflow.entity.TransactionStatus;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponseDto {
    private Long id;
    private Long merchantId;
    private BigDecimal amount;
    private String currency;
    private TransactionStatus status;
    private LocalDateTime createdAt;
}