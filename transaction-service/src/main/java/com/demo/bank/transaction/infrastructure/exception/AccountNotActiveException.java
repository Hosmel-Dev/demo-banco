package com.demo.bank.transaction.infrastructure.exception;

import com.demo.bank.transaction.infrastructure.adapter.out.http.account.AccountServiceProblem;
import lombok.Getter;

@Getter
public class AccountNotActiveException extends InfrastructureException {
    private final Long accountId;
    public AccountNotActiveException(Long accountId) {
        super("ACCOUNT_NOT_ACTIVE","El estado de la cuenta %s no está activa"
                .formatted(accountId));
        this.accountId = accountId;
    }

}
