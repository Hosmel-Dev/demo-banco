package com.demo.bank.account.domain.exception;

public class LedgerConflictException extends AccountBusinessException {

    public LedgerConflictException(String message) {
        super("LEDGER_CONFLICT", message);
    }
}