package com.demo.bank.account.infrastructure.adapter.out.persistance.entity;

import com.demo.bank.account.domain.enums.AppCurrency;
import com.demo.bank.account.domain.enums.Direction;
import com.demo.bank.account.domain.enums.EntryKind;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table("ledger_entries")
public class LedgerEntryEntity {
    @Id
    private Long id;

    @Column("transaction_id")
    private Long transactionId;

    @Column("account_id")
    private Long accountId;

    @Column("direction")
    private Direction direction;

    @Column("amount")
    private BigDecimal amount;

    @Column("currency")
    private AppCurrency currency;

    @Column("balance_after")
    private BigDecimal balanceAfter;

    @Column("entry_kind")
    private EntryKind entryKind;

    @Column("reverses_entry_id")
    private Long reversesEntryId;

    @Column("description")
    private String description;

    @Column("created_at")
    private LocalDateTime createdAt;
}