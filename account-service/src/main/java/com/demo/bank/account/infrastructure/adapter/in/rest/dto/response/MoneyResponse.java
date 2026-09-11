package com.demo.bank.account.infrastructure.adapter.in.rest.dto.response;

import com.demo.bank.account.domain.enums.AppCurrency;

import java.math.BigDecimal;

public record MoneyResponse(
        BigDecimal amount,
        AppCurrency currency
) {
}
