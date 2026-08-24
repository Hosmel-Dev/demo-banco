package com.demo.bank.transaction.infrastructure.adapter.out.persistance.http.account;

import com.demo.bank.transaction.domain.enums.AppCurrency;
import com.demo.bank.transaction.domain.model.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ValidationAccountResponse (
        Long id,
        String accountNumber,
        BigDecimal amount,
        AppCurrency currency,
        LocalDateTime createdAt,
        String status
)
{}
