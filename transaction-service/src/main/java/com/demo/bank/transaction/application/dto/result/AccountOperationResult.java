package com.demo.bank.transaction.application.dto.result;

import com.demo.bank.transaction.domain.enums.AppCurrency;

import java.math.BigDecimal;

public record AccountOperationResult(
        Long id,
        BigDecimal amount,
        AppCurrency currency
) {}
