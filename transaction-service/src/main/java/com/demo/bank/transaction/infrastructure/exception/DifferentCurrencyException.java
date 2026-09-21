package com.demo.bank.transaction.infrastructure.exception;

import com.demo.bank.transaction.infrastructure.adapter.out.http.account.AccountServiceProblem;
import lombok.Getter;

@Getter
public class DifferentCurrencyException extends InfrastructureException {
    public DifferentCurrencyException() {
        super("WRONG_CURRENCY","La cuenta de origen y de destino no cuentan con el mismo tipo de moneda" );
    }
}
