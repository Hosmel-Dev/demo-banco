package com.demo.bank.transaction.infrastructure.exception;

import lombok.Getter;

@Getter
public class AccountNotActiveException extends InfrastructureException {
    private final Long accountId;
    private final String accountStatus;
    public AccountNotActiveException(String accountStatus) {
        super("ACCOUNT_NOT_ACTIVE", "La cuenta de destino tiene un estado de %s"
                .formatted(accountStatus));
        this.accountStatus = accountStatus;
        this.accountId = null;
    }

    public AccountNotActiveException(Long accountId) {
        super("ACCOUNT_NOT_ACTIVE","El estado de la cuenta %s no es activa"
                .formatted(accountId));
        this.accountStatus = null;
        this.accountId = accountId;
    }

}
