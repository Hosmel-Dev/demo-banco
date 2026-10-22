package com.demo.bank.account.domain.exception;

public class ForbiddenAccessException extends AccountBusinessException {
    public ForbiddenAccessException() {
        super("FORBIDDEN_ACCESS", "La acción que trata de realizar no está permitida");
    }
}
