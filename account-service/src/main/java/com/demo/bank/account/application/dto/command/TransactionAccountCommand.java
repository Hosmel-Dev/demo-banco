package com.demo.bank.account.application.dto.command;

import com.demo.bank.account.domain.enums.AppCurrency;
import java.math.BigDecimal;

public record TransactionAccountCommand (
        Long id,
        BigDecimal amount,
        AppCurrency currency
){}
