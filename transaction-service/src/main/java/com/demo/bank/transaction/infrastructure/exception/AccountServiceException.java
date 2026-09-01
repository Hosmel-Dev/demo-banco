package com.demo.bank.transaction.infrastructure.exception;

public class AccountServiceException extends InfrastructureException {
    public AccountServiceException() {
        super("CONNECTION_ERROR","Hubo un error al comunicarse con el servicio de cuentas");
    }
}
