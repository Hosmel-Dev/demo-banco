package com.demo.bank.account.domain.model;

import com.demo.bank.account.domain.enums.AccountStatus;
import com.demo.bank.account.domain.enums.AccountType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Account {
    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private Money balance;
    private AccountStatus status;
    private LocalDateTime createdAt;

}
