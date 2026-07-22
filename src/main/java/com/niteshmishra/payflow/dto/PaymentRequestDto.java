package com.niteshmishra.payflow.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequestDto {
    private Long merchantId;
    private BigDecimal amount;
    private String currency;
    private String idempotencyKey;
}