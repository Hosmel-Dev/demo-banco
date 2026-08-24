package com.demo.bank.transaction.application.dto.result;

import com.demo.bank.transaction.domain.model.Money;

public record AccountTransferResult(
        Long id,
        Money money
) {}
