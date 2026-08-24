package com.demo.bank.transaction.infrastructure.adapter.out.persistance.entity;

import com.demo.bank.transaction.domain.enums.AppCurrency;
import com.demo.bank.transaction.domain.enums.Direction;
import com.demo.bank.transaction.domain.model.Money;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("ledger_entries")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
public class LedgerEntryEntity {
    @Id
    @Column("id")
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
    @Column("created_at")
    private LocalDateTime createdAt;
}
