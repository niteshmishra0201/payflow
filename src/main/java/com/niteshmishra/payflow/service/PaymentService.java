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
    public PaymentResponseDto createPayment(PaymentRequestDto request, Long authenticatedMerchantId) {

        Optional<Transaction> existing =
                transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existing.isPresent()) {
            return transactionMapper.toResponseDto(existing.get());
        }

        Merchant merchant = merchantRepository.findById(authenticatedMerchantId)
                .orElseThrow(() -> new MerchantNotFoundException(
                        "No merchant found with id " + authenticatedMerchantId));

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

        createLedgerEntries(savedTransaction);
        creditMerchantWallet(merchant, savedTransaction.getAmount());

        savedTransaction.setStatus(TransactionStatus.SUCCESS);
        savedTransaction = transactionRepository.save(savedTransaction);

        return transactionMapper.toResponseDto(savedTransaction);
    }

    private void createLedgerEntries(Transaction transaction) {
        LedgerEntry debitEntry = new LedgerEntry();
        debitEntry.setTransaction(transaction);
        debitEntry.setAccountType(LedgerAccountType.CUSTOMER_SUSPENSE);
        debitEntry.setEntryType(LedgerEntryType.DEBIT);
        debitEntry.setAmount(transaction.getAmount());
        ledgerEntryRepository.save(debitEntry);

        LedgerEntry creditEntry = new LedgerEntry();
        creditEntry.setTransaction(transaction);
        creditEntry.setAccountType(LedgerAccountType.MERCHANT_WALLET);
        creditEntry.setEntryType(LedgerEntryType.CREDIT);
        creditEntry.setAmount(transaction.getAmount());
        ledgerEntryRepository.save(creditEntry);
    }

    private void creditMerchantWallet(Merchant merchant, BigDecimal amount) {
        Wallet wallet = walletRepository.findByMerchantIdForUpdate(merchant.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "No wallet found for merchant id " + merchant.getId()));

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }
}