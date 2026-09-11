package com.demo.bank.account.application.dto.result;

import com.demo.bank.account.domain.model.Money;

import java.time.LocalDateTime;

public record TransactionAccountResult(
        Long id,
        String accountNumber,
        Money balance,
        LocalDateTime createdAt
) {}
