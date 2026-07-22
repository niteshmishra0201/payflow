package com.niteshmishra.payflow.repository;

import com.niteshmishra.payflow.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByMerchantId(Long merchantId);
}