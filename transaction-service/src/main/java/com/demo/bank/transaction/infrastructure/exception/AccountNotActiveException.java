package com.demo.bank.transaction.infrastructure.exception;

public class AccountNotActiveException extends InfrastructureException {
    public AccountNotActiveException(Long id) {
        super("ACCOUNT_NOT_ACTIVE","El estado de la cuenta %s no está activa"
                .formatted(id));
    }

    public AccountNotActiveException(String  id) {
        super("ACCOUNT_NOT_ACTIVE","El estado de la cuenta %s no está activa"
                .formatted(id));
    }
}
