package com.demo.bank.transaction.infrastructure.exception;

import com.demo.bank.transaction.domain.exception.TransactionBusinessException;
import com.demo.bank.transaction.infrastructure.adapter.out.http.account.AccountServiceProblem;
import lombok.Getter;

@Getter
public class AccountNotFoundException extends TransactionBusinessException {
    private final String accountNumber;
    private final Long accountId;
    public AccountNotFoundException(String accountNumber) {
        super("ACCOUNT_NOT_FOUND",
                "La cuenta con el numero %s no existe"
                        .formatted(accountNumber));
        this.accountNumber = accountNumber;
        this.accountId = null;
    }

    public AccountNotFoundException(Long accountId) {
        super("ACCOUNT_NOT_FOUND",
                "La cuenta con id %s no existe"
                        .formatted(accountId));
        this.accountId = accountId;
        this.accountNumber = null;
    }
}
