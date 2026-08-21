package com.demo.bank.account.domain.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InsufficientFundsException extends AccountBusinessException {
    private final String accountNumber;
    private final BigDecimal balance;
    private final BigDecimal amount;

    public InsufficientFundsException(String accountNumber, BigDecimal balance, BigDecimal amount){
        super("INSUFFICIENT_FUNDS","La cuenta %s no cuenta con suficiente saldo %s"
                .formatted(accountNumber, balance));
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.amount = amount;
    }

}
