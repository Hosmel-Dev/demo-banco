package com.demo.bank.transaction.infrastructure.exception;

public class DifferentCurrencyException extends InfrastructureException {
    public DifferentCurrencyException() {
        super("WRONG_CURRENCY","La cuenta de origen y de destino no cuentan con el mismo tipo de moneda" );
    }
}
