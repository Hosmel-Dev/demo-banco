package com.demo.bank.transaction.infrastructure.adapter.out.http.account;

import java.net.URI;
import java.time.Instant;

public record AccountServiceProblem(
        URI type,
        String title,
        Integer status,
        String detail,
        URI instance,
        String code,
        Instant timestamp,
        String accountNumber
) {
}
