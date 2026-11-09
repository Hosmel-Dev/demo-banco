package com.demo.bank.account.domain.model;

import com.demo.bank.account.domain.enums.AppCurrency;
import com.demo.bank.account.domain.enums.Direction;
import com.demo.bank.account.domain.enums.EntryKind;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LedgerEntry(
        Long id,
        Long transactionId,
        Long accountId,
        Direction direction,
        BigDecimal amount,
        AppCurrency currency,
        BigDecimal balanceAfter,
        EntryKind entryKind,
        Long reversesEntryId,
        LocalDateTime createdAt
) {}