package com.demo.bank.transaction.infrastructure.exception;

import lombok.Getter;

@Getter
public class InfrastructureException extends RuntimeException {
    private final String code;
    public InfrastructureException(String code, String message) {
        super(message);
        this.code = code;
    }
}
