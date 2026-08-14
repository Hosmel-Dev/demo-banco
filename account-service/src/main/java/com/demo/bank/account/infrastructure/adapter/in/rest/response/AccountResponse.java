package com.demo.bank.account.infrastructure.adapter.in.rest.response;

import com.demo.bank.account.domain.enums.AccountStatus;
import com.demo.bank.account.domain.enums.AccountType;
import com.demo.bank.account.domain.enums.AppCurrency;
import com.demo.bank.account.domain.model.AccountBalance;

import java.math.BigDecimal;

public record AccountResponse (
        Long id,
        String accountNumber,
        AccountType type,
        BigDecimal balance,
        AppCurrency currency,
        AccountStatus status

){
}
