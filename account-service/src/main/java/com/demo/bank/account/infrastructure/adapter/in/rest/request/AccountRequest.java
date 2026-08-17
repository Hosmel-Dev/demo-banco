package com.demo.bank.account.infrastructure.adapter.in.rest.request;

import com.demo.bank.account.domain.enums.AccountStatus;
import com.demo.bank.account.domain.enums.AccountType;
import com.demo.bank.account.domain.enums.AppCurrency;

import java.math.BigDecimal;

public record AccountRequest (
        Long id,
        String accountNumber,
        AccountType type,
        BigDecimal balance,
        AppCurrency currency,
        AccountStatus status
){
}
