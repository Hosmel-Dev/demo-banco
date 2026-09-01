package com.demo.bank.transaction.infrastructure.exception;

public class AccountServiceUnavailableException extends InfrastructureException {
    public AccountServiceUnavailableException() {
        super("UNAVAILABLE_SERVICE","El servicio de cuentas no está disponible");
    }
}
