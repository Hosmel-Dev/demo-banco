package com.demo.bank.account.domain.model;

import com.demo.bank.account.domain.enums.AppCurrency;

import java.math.BigDecimal;
import java.util.Objects;


public record AccountBalance(BigDecimal balance, AppCurrency currency){

    public AccountBalance {
        Objects.requireNonNull(balance, "El saldo no puede ser Null");
        Objects.requireNonNull(currency, "La moneda no puede ser Null");
        if(balance.compareTo(BigDecimal.ZERO)<0){
            throw new IllegalArgumentException("El saldo no puede ser negativo");
        }
    }

    public AccountBalance credit(BigDecimal amount){
        validateAmount(amount);

        return new AccountBalance(
                balance.add(amount),
                currency
        );
    }

    public AccountBalance debit(BigDecimal amount){
        validateAmount(amount);

        BigDecimal newBalance = balance.subtract(amount);

        if(newBalance.compareTo(BigDecimal.ZERO)<0){
            throw new IllegalArgumentException("AccountBalance Insuficiente");
        }

        return new AccountBalance(
                newBalance,
                currency
        );

    }

    private static void validateAmount(BigDecimal amount){
        Objects.requireNonNull(amount, "El monto no puede ser nulo");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
    }
}
