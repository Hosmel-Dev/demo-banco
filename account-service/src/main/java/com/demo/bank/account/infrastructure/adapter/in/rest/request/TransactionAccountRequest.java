package com.demo.bank.account.infrastructure.adapter.in.rest.request;

import com.demo.bank.account.domain.enums.AppCurrency;
import java.math.BigDecimal;

public record TransactionAccountRequest(
        Long id,
        BigDecimal amount,
        AppCurrency currency
) {
}
