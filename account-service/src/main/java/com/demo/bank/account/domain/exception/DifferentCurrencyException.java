package com.demo.bank.account.domain.exception;

import com.demo.bank.account.domain.enums.AppCurrency;
import lombok.Getter;

@Getter
public class DifferentCurrencyException extends AccountBusinessException {
    private final String accountNumber;
    private final AppCurrency currencyEntrance;
    private final AppCurrency currencyAccount;

    public DifferentCurrencyException(String accountNumber, AppCurrency currencyEntrance, AppCurrency currencyAccount){
        super("WRONG_CURRENCY","La divisa de entrada es %s y la divisa de la cuenta es %s"
                .formatted(currencyEntrance, currencyAccount));
        this.currencyAccount = currencyAccount;
        this.currencyEntrance = currencyEntrance;
        this.accountNumber = accountNumber;
    }
}
