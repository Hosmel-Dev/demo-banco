package com.demo.bank.transaction.application.dto.result;

import com.demo.bank.transaction.domain.enums.AppCurrency;

public record AccountValidationResult(
        Long id,
        String status,
        AppCurrency currency
) {}
