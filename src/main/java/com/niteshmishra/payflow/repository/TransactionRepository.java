package com.niteshmishra.payflow.repository;

import com.niteshmishra.payflow.entity.Transaction;
import com.niteshmishra.payflow.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
    List<Transaction> findByMerchantIdAndStatus(Long merchantId, TransactionStatus status);
}