package com.demo.bank.account.domain.model;

import com.demo.bank.account.domain.enums.AccountStatus;
import com.demo.bank.account.domain.enums.AccountType;
import com.demo.bank.account.domain.enums.AppCurrency;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Account {
    private Long id;
    private String accountNumber;
    private AccountType type;
    private AccountBalance balance;
    private AccountStatus status;

}
