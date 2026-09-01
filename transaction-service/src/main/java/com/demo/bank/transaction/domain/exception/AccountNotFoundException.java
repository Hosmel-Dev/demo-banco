package com.demo.bank.transaction.domain.exception;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends TransactionBusinessException {
    private final String accountNumber;
    private final Long accountId;
    public AccountNotFoundException(String accountNumber) {
        super("DESTINY_ACCOUNT_NOT_FOUND",
                "La cuenta con el numero %s no existe"
                        .formatted(accountNumber));
        this.accountNumber = accountNumber;
        this.accountId = null;
    }

    public AccountNotFoundException(Long accountId) {
        super("DESTINY_ACCOUNT_NOT_FOUND",
                "La cuenta con id %s no existe"
                        .formatted(accountId));
        this.accountId = accountId;
        this.accountNumber = null;
    }
}
