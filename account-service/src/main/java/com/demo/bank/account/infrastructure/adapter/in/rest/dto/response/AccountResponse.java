package com.demo.bank.account.infrastructure.adapter.in.rest.dto.response;

import com.demo.bank.account.domain.enums.AccountStatus;
import com.demo.bank.account.domain.enums.AccountType;

public record AccountResponse (
        Long id,
        String accountNumber,
        AccountType accountType,
        MoneyResponse balance,
        AccountStatus status
){
}
