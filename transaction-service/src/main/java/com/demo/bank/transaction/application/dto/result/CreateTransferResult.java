package com.demo.bank.transaction.application.dto.result;

import com.demo.bank.transaction.domain.enums.AppCurrency;
import com.demo.bank.transaction.domain.enums.TransactionStatus;
import com.demo.bank.transaction.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateTransferResult(
        Long transactionId,
        String idempotencyKey,
        TransactionType type,
        BigDecimal amount,
        AppCurrency currency,
        TransactionStatus status,
        Long fromAccountId,
        Long toAccountId,
        LocalDateTime createdAt
) {}
