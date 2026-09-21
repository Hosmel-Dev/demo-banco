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

    public Money credit(BigDecimal amountEntrance){
        validateAmount(amountEntrance);

        return new Money(
                amount.add(amountEntrance),
                currency
        );
    }

    public Boolean validForDebit(BigDecimal amountEntrance){
        validateAmount(amountEntrance);
        BigDecimal newBalance = amount.subtract(amountEntrance);
        return newBalance.compareTo(BigDecimal.ZERO) >= 0;

    }

    private static void validateAmount(BigDecimal amountEntrance){
        Objects.requireNonNull(amountEntrance, "El monto no puede ser nulo");
        if (amountEntrance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
    }
}
