package com.demo.bank.transaction.application.port.out;

import com.demo.bank.transaction.domain.model.LedgerEntry;
import reactor.core.publisher.Mono;

public interface LedgerEntriesRepositoryPortOut {
    Mono<LedgerEntry> save(LedgerEntry ledgerEntry);
    Mono<LedgerEntry> getById(Long id);
    Mono<LedgerEntry> findByTransaction(Long id);
}
