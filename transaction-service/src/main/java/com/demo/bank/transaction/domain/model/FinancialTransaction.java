package com.demo.bank.transaction.domain.model;

import com.demo.bank.transaction.domain.enums.TransactionStatus;
import com.demo.bank.transaction.domain.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FinancialTransaction {
    private Long id;
    private String idempotency_key;
    private TransactionType type;
    private Money money;
    private TransactionStatus status;
    private String description;
    private LocalDateTime createdAt;
}
