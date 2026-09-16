package com.demo.bank.transaction.infrastructure.adapter.in.rest.request;

import com.demo.bank.transaction.domain.enums.AppCurrency;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record CreateTransferRequest(
        @NotNull(message = "El id de la cuenta de origen es obligatorio")
        @Positive(message = "El id de la cuenta debe ser un número positivo")
        Long originAccountId,
        @NotBlank(message = "El número de cuenta es obligatorio")
        @Size(min = 10, max = 14, message = "El número de cuenta debe tener entre 10 y 14 caracteres")
        @Pattern(regexp = "\\d+")
        String destinationAccountNumber,
        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.1", message = "El saldo mínimo debe ser 0.1")
        BigDecimal amount,
        @NotNull(message = "La divisa es obligatoria")
        AppCurrency currency,
        @Length(max = 200)
        String description
) {}
