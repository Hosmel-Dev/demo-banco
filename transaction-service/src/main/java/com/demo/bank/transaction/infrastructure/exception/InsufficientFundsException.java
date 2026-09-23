package com.demo.bank.transaction.infrastructure.exception;

import lombok.Getter;

@Getter
public class InsufficientFundsException extends InfrastructureException {
    private final Long accountId;
    public InsufficientFundsException(Long accountId) {
        super("INSUFFICIENT_FUNDS","La cuenta %s no cuenta con suficiente saldo"
                .formatted(accountId));
        this.accountId = accountId;
    }

}
