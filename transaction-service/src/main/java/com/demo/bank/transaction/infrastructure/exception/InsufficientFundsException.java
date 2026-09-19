package com.demo.bank.transaction.infrastructure.exception;

public class InsufficientFundsException extends InfrastructureException {
    public InsufficientFundsException() {
        super("INSUFFICIENT_FUNDS","La cuenta %s no cuenta con suficiente saldo %s"
                .formatted(1, 1));
    }
}
