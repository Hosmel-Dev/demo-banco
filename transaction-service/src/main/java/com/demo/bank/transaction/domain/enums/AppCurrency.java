package com.demo.bank.transaction.domain.enums;

import lombok.Getter;

import java.util.Currency;

@Getter
public enum AppCurrency {
    USD(Currency.getInstance("USD")),
    PEN(Currency.getInstance("PEN"));

    private final Currency currency;

    AppCurrency(Currency currency) {
        this.currency = currency;
    }
}
