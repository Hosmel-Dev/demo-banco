package com.demo.bank.account.domain.model;

import com.demo.bank.account.domain.enums.AppCurrency;

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

    public Money credit(BigDecimal amount){
        validateAmount(amount);

        return new Money(
                amount.add(amount),
                currency
        );
    }

    public Boolean validForDebit(BigDecimal amount){
        validateAmount(amount);
        BigDecimal newBalance = amount.subtract(amount);
        return newBalance.compareTo(BigDecimal.ZERO) >= 0;

    }

    private static void validateAmount(BigDecimal amount){
        Objects.requireNonNull(amount, "El monto no puede ser nulo");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
    }
}
