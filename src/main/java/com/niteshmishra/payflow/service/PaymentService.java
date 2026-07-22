package com.niteshmishra.payflow.service;

import com.niteshmishra.payflow.dto.PaymentRequestDto;
import com.niteshmishra.payflow.dto.PaymentResponseDto;
import com.niteshmishra.payflow.entity.*;
import com.niteshmishra.payflow.exception.MerchantNotFoundException;
import com.niteshmishra.payflow.mapper.TransactionMapper;
import com.niteshmishra.payflow.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final MerchantRepository merchantRepository;
    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final TransactionMapper transactionMapper;

    public PaymentService(TransactionRepository transactionRepository,
                          MerchantRepository merchantRepository,
                          WalletRepository walletRepository,
                          LedgerEntryRepository ledgerEntryRepository,
                          TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.merchantRepository = merchantRepository;
        this.walletRepository = walletRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto request) {

        Optional<Transaction> existing =
                transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existing.isPresent()) {
            return transactionMapper.toResponseDto(existing.get());
        }

        Merchant merchant = merchantRepository.findById(request.getMerchantId())
                .orElseThrow(() -> new MerchantNotFoundException(
                        "No merchant found with id " + request.getMerchantId()));

        Transaction transaction = new Transaction();
        transaction.setMerchant(merchant);
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setIdempotencyKey(request.getIdempotencyKey());
        transaction.setStatus(TransactionStatus.CREATED);

        Transaction savedTransaction;
        try {
            savedTransaction = transactionRepository.save(transaction);
        } catch (DataIntegrityViolationException e) {
            return transactionMapper.toResponseDto(
                    transactionRepository.findByIdempotencyKey(request.getIdempotencyKey())
                            .orElseThrow(() -> new IllegalStateException(
                                    "Transaction vanished after constraint violation — should never happen")));
        }

        // Ledger entries + wallet update come in Microstep 3.5

        return transactionMapper.toResponseDto(savedTransaction);
    }

} // ← this closing brace ends the PaymentService class itself