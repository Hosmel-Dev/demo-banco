package com.demo.bank.account.infrastructure.adapter.in.rest.dto.response;

import java.time.LocalDateTime;

public record TransactionAccountResponse(
        Long id,
        String accountNumber,
        MoneyResponse balance,
        LocalDateTime createdAt
) {
}
