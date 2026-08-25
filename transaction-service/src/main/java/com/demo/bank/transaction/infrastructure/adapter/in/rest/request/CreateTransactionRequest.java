package com.demo.bank.transaction.infrastructure.adapter.in.rest.request;

import com.demo.bank.transaction.domain.enums.AppCurrency;

import java.math.BigDecimal;

public record CreateTransactionRequest (
        Long accountId,
        BigDecimal amount,
        AppCurrency currency,
        String description
){}
