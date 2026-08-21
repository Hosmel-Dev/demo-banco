package com.demo.bank.transaction.domain.model;
import com.demo.bank.transaction.domain.enums.AppCurrency;

import java.math.BigDecimal;
import java.util.Objects;


public record Money(BigDecimal amount, AppCurrency currency){

    public Money {
        Objects.requireNonNull(amount, "El saldo no puede ser Null");
        Objects.requireNonNull(currency, "La moneda no puede ser Null");
        if(amount.compareTo(BigDecimal.ZERO)<0){
            throw new IllegalArgumentException("El saldo no puede ser negativo");
        }
    }

}
