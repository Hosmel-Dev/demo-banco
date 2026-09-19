package com.demo.bank.transaction.infrastructure.adapter.out.persistence.repository;

import com.demo.bank.transaction.infrastructure.adapter.out.persistence.entity.LedgerEntryEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

public interface ReactiveLedgerEntryRepository extends ReactiveCrudRepository<LedgerEntryEntity, Long> {
    @Query("SELECT * FROM ledger_entries WHERE transaction_id = :transactionId AND direction = :direction")
    Mono<LedgerEntryEntity> findByTransactionIdAndDirection(Long transactionId, String direction);
}
