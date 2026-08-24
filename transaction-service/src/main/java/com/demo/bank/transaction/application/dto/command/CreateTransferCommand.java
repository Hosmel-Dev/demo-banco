package com.demo.bank.transaction.application.dto.command;

import com.demo.bank.transaction.domain.enums.AppCurrency;

import java.math.BigDecimal;

public record CreateTransferCommand(
        String idempotencyKey,
        FinancialTransaction transaction,
        LedgerEntries ledgerEntries
) {
    public record FinancialTransaction(
            BigDecimal amount,
            AppCurrency currency,
            String description
            
    ){}
    public record LedgerEntries(
            Long originAccountId,
            String destinationAccountNumber
    ){}
}
