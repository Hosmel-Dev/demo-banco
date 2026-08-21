package com.demo.bank.account.infrastructure.adapter.in.rest.response;

import com.demo.bank.account.domain.model.AccountBalance;

import java.time.LocalDateTime;

public record TransactionAccountResponse(
        Long id,
        String accountNumber,
        AccountBalance balance,
        LocalDateTime createdAt
) {
}
