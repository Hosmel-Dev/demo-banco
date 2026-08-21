package com.demo.bank.transaction.infrastructure.adapter.in.rest.request;

import com.demo.bank.transaction.domain.enums.AppCurrency;

import java.math.BigDecimal;

public record CreateTransferRequest(
        Long originAccountId,
        Long destinationAccountId,
        BigDecimal amount,
        AppCurrency currency,
        String description
) {}
