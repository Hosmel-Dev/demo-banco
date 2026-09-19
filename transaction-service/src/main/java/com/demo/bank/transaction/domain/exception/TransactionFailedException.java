package com.demo.bank.transaction.domain.exception;

import lombok.Getter;

@Getter
public class TransactionFailedException extends TransactionBusinessException {
  private final Long transactionId;
    public TransactionFailedException(Long transactionId) {
        super("FAILED_TRANSACTION","La transacción con id: %s falló, inténtelo más tarde"
                .formatted(transactionId));
        this.transactionId = transactionId;
    }
}
