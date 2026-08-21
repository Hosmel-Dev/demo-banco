package com.demo.bank.account.infrastructure.adapter.in.rest.request;

import com.demo.bank.account.domain.enums.AccountStatus;
import com.demo.bank.account.domain.enums.AccountType;
import com.demo.bank.account.domain.enums.AppCurrency;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AccountRequest(
        Long id,
        @NotBlank(message = "El número de cuenta es obligatorio")
        @Size(min = 10, max = 14, message = "El número de cuenta debe tener entre 10 y 14 caracteres")
        String accountNumber,

        @NotNull(message = "El tipo de cuenta es obligatorio")
        AccountType type,

        @NotNull(message = "El saldo inicial es obligatorio")
        @Min(value = 0, message = "El saldo mínimo debe ser 0.1")
        BigDecimal balance,

        @NotNull(message = "La divisa es obligatoria")
        AppCurrency currency,

        @NotNull(message = "El estado de la cuenta es obligatorio")
        AccountStatus status
) {
}
