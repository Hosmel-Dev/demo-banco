package com.demo.bank.transaction.domain.model;

import com.demo.bank.transaction.domain.enums.Direction;
import com.demo.bank.transaction.domain.enums.EntryKind;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Data
@Builder
public class LedgerEntry {
    private Long id;
    private Long transactionId;
    private Long accountId;
    private Direction direction;
    private Money money;
    private LocalDateTime createdAt;
    private EntryKind entryKind;
    private Long reversesEntryId;
    private String description;
}
