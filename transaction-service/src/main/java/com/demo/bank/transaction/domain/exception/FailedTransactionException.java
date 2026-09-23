package com.demo.bank.transaction.domain.exception;

import lombok.Getter;

@Getter
public class FailedTransactionException extends TransactionBusinessException {
    private final Long transactionId;
    public FailedTransactionException(Long transactionId) {
        super("TRANSACTION_FAILED","La transacción de id %s falló al ejecutarse inténtelo en unos minutos"
                .formatted(transactionId));
        this.transactionId = transactionId;
    }
}
