package com.demo.bank.transaction.infrastructure.adapter.in.rest.request;

import com.demo.bank.transaction.domain.enums.AppCurrency;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record CreateTransactionRequest (
        @NotNull(message = "El id de la cuenta es obligatorio")
        @Positive(message = "El id de la cuenta debe ser un número positivo")
        Long accountId,
        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El saldo mínimo debe ser 0.01")
        @DecimalMax(value = "100000.00", message = "El valor máximo de débito es de 100,000.00")
        BigDecimal amount,
        @NotNull(message = "La divisa es obligatoria")
        AppCurrency currency,
        @Length(max = 200)
        String description
){}
