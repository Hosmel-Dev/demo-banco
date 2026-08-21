package com.demo.bank.account.domain.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InsufficientFundsException extends AccountBusinessException {
    private final String accountNumber;
    private final BigDecimal balance;

    public InsufficientFundsException(String accountNumber, BigDecimal balance){
        super("INSUFFICIENT_FUNDS","La cuenta %s no cuenta con suficiente saldo %s"
                .formatted(accountNumber, balance));
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

}
