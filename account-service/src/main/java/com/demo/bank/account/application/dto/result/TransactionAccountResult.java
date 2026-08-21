package com.demo.bank.account.application.dto.result;

import com.demo.bank.account.domain.model.AccountBalance;

import java.time.LocalDateTime;

public record TransactionAccountResult(
        Long id,
        String accountNumber,
        AccountBalance balance,
        LocalDateTime createdAt
) {}
