package com.demo.bank.transaction.domain.exception;

public class CreditLedgerException extends TransactionBusinessException {
    public CreditLedgerException() {
        super("CREDIT_LEDGER_FAILED","Error al almacenar el ledger de crédito");
    }
}
