package com.demo.bank.account.infrastructure.adapter.in.rest.dto.request;

import com.demo.bank.account.domain.enums.AppCurrency;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransactionAccountRequest(
        @NotNull(message = "El id de la cuenta es obligatorio")
        @Positive(message = "El id de la cuenta debe ser un número positivo")
        Long id,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El valor mínimo de transferencia debe ser 0.01")
        @DecimalMax(value = "100000.00", message = "El valor máximo de débito es de 100,000.00")
        BigDecimal amount,

        @NotNull(message = "La divisa es obligatoria")
        AppCurrency currency
) {
}
