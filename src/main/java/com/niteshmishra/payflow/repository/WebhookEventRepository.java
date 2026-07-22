package com.niteshmishra.payflow.repository;

import com.niteshmishra.payflow.entity.WebhookEvent;
import com.niteshmishra.payflow.entity.WebhookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {
    List<WebhookEvent> findByStatus(WebhookStatus status);
}