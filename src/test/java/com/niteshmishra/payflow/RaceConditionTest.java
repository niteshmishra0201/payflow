package com.niteshmishra.payflow;

import com.niteshmishra.payflow.dto.PaymentRequestDto;
import com.niteshmishra.payflow.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class RaceConditionTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void simulateConcurrentPayments() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    PaymentRequestDto request = new PaymentRequestDto();
                    request.setMerchantId(1L);
                    request.setAmount(BigDecimal.valueOf(100));
                    request.setCurrency("INR");
                    request.setIdempotencyKey("race_test_" + index);
                    paymentService.createPayment(request);
                } catch (Exception e) {
                    System.out.println("Thread " + index + " failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
    }
}