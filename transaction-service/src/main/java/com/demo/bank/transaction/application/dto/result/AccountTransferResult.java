package com.demo.bank.transaction.application.dto.result;

import com.demo.bank.transaction.domain.enums.AppCurrency;
import com.demo.bank.transaction.domain.model.Money;

import java.math.BigDecimal;

public record AccountTransferResult(
        Long id,
        BigDecimal amount,
        AppCurrency currency
) {}
