package com.demo.bank.transaction.infrastructure.adapter.out.persistance.entity;

import com.demo.bank.transaction.domain.enums.AppCurrency;
import com.demo.bank.transaction.domain.enums.TransactionStatus;
import com.demo.bank.transaction.domain.enums.TransactionType;
import com.demo.bank.transaction.domain.model.Money;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("financial_transactions")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
public class FinancialTransactionEntity {
    @Id
    @Column("id")
    private Long id;
    @Column("idempotency_key")
    private String idempotencyKey;
    @Column("type")
    private TransactionType type;
    @Column("amount")
    private BigDecimal amount;
    @Column("currency")
    private AppCurrency currency;
    @Column("status")
    private TransactionStatus status;
    @Column("description")
    private String description;
    @Column("created_at")
    private LocalDateTime createdAt;
}
