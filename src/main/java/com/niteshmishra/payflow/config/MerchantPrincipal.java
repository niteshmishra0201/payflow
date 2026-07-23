package com.niteshmishra.payflow.config;

import com.niteshmishra.payflow.entity.Merchant;
import lombok.Getter;

@Getter
public class MerchantPrincipal {
    private final Long merchantId;
    private final String merchantName;

    public MerchantPrincipal(Merchant merchant) {
        this.merchantId = merchant.getId();
        this.merchantName = merchant.getName();
    }
}