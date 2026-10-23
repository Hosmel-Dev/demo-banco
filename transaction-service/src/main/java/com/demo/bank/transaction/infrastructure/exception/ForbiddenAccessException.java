package com.demo.bank.transaction.infrastructure.exception;

public class ForbiddenAccessException extends InfrastructureException {
    public ForbiddenAccessException() {
        super("FORBIDDEN_ACCESS", "La acción que trata de realizar no está permitida");
    }
}

