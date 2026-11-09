package com.demo.bank.transaction.infrastructure.exception;

public class AccountLedgerConflictException extends InfrastructureException {
    public AccountLedgerConflictException() {
        super("LEDGER_CONFLICT",
                "El movimiento ya existe con datos distintos");
    }
}
