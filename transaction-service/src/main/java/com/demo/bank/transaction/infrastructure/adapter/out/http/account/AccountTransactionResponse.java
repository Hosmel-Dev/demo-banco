package com.demo.bank.transaction.infrastructure.adapter.out.http.account;

import com.demo.bank.transaction.domain.enums.AppCurrency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountTransactionResponse(
    @NotNull
    Long id,
    @NotBlank(message = "El número de cuenta es obligatorio")
    @Size(min = 10, max = 14, message = "El número de cuenta debe tener entre 10 y 14 caracteres")
    String accountNumber,
    MoneyResponse balance,
    LocalDateTime createdAt
){
    public record MoneyResponse(
            @NotNull
            BigDecimal amount,
            @NotBlank
            AppCurrency currency
    ){}
}
