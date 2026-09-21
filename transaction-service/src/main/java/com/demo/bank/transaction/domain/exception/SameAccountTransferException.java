package com.demo.bank.transaction.domain.exception;

import lombok.Getter;

@Getter
public class SameAccountTransferException extends TransactionBusinessException {
    private final Long accountId;
    public SameAccountTransferException(Long accountId) {
        super("SAME_ACCOUNT_TRANSFER_NOT_ALLOWED","Las cuentas de origen y destino deben ser diferentes");
        this.accountId = accountId;
    }
}
