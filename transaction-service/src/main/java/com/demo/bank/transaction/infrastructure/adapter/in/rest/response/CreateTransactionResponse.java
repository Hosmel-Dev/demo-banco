package com.demo.bank.transaction.infrastructure.adapter.in.rest.response;

import com.demo.bank.transaction.domain.enums.AppCurrency;
import com.demo.bank.transaction.domain.enums.TransactionStatus;
import com.demo.bank.transaction.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateTransactionResponse(
        Long transactionId,
        String idempotencyKey,
        TransactionType type,
        BigDecimal amount,
        AppCurrency currency,
        TransactionStatus status,
        Long accountId,
        LocalDateTime createdAt
) {}
