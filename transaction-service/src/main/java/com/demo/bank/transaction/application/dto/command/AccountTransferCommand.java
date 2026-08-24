package com.demo.bank.transaction.application.dto.command;

import com.demo.bank.transaction.domain.model.Money;

public record AccountTransferCommand(
        Long id,
        Money money
) {}
