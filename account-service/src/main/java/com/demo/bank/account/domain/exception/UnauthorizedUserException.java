package com.demo.bank.account.domain.exception;

public class UnauthorizedUserException extends AccountBusinessException {
    public UnauthorizedUserException() {
        super("UNAUTHORIZED_USER", "Las credenciales no son válidas");
    }
}
