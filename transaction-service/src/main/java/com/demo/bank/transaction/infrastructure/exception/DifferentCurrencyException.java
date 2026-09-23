package com.demo.bank.transaction.infrastructure.exception;

import com.demo.bank.transaction.infrastructure.adapter.out.http.account.AccountServiceProblem;
import lombok.Getter;

@Getter
public class DifferentCurrencyException extends InfrastructureException {
    private final String currencyOriginAccount;
    private final String currencyDestinationAccount;
    public DifferentCurrencyException(String currencyOriginAccount, String currencyDestinationAccount) {
        super("WRONG_CURRENCY","La cuenta de destino cuenta con un tipo de moneda distinto al de origen");
        this.currencyOriginAccount = currencyOriginAccount;
        this.currencyDestinationAccount = currencyDestinationAccount;
    }

    public DifferentCurrencyException() {
        super("WRONG_CURRENCY","La cuenta de origen y de destino no cuentan con el mismo tipo de moneda" );
        this.currencyOriginAccount = null;
        this.currencyDestinationAccount = null;
    }
}
