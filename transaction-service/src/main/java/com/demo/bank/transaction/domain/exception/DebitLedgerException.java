package com.demo.bank.transaction.domain.exception;

public class DebitLedgerException extends TransactionBusinessException {
    public DebitLedgerException() {
        super("DEBIT_LEDGER_FAILED","Error al almacenar el ledger de débito");
    }
}
