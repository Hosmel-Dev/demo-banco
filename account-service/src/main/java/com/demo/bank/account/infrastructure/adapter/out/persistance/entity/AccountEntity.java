package com.demo.bank.account.infrastructure.adapter.out.persistance.entity;

import com.demo.bank.account.domain.enums.AccountStatus;
import com.demo.bank.account.domain.enums.AccountType;
import com.demo.bank.account.domain.enums.AppCurrency;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("account")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
public class AccountEntity {
    @Id
    @Column("id")
    private Long id;

    @Column("account_number")
    private String accountNumber;

    @Column("account_type")
    private AccountType accountType;

    @Column("balance")
    private BigDecimal balance;

    @Column("currency")
    private AppCurrency currency;

    @Column("status")
    private AccountStatus status;

    @Column("created_at")
    private LocalDateTime createdAt;
}
