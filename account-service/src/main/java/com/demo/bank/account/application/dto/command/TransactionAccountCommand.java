package com.demo.bank.account.application.dto.command;

import com.demo.bank.account.domain.enums.AppCurrency;
import com.demo.bank.account.domain.enums.EntryKind;

import java.math.BigDecimal;

public record TransactionAccountCommand (
        Long id,
        Long transactionId,
        EntryKind entryKind,
        BigDecimal amount,
        AppCurrency currency
){}
