package com.demo.bank.transaction.application.port.out;

import com.demo.bank.transaction.domain.model.FinancialTransaction;
import reactor.core.publisher.Mono;

public interface TransactionRepositoryPortOut {
    Mono<FinancialTransaction> save(FinancialTransaction financialTransaction);
}
