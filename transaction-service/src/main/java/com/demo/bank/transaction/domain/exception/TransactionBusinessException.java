package com.demo.bank.transaction.domain.exception;

import lombok.Getter;

@Getter
public class TransactionBusinessException extends RuntimeException {
    private final String code;
    public TransactionBusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}
