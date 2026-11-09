package com.demo.bank.account.infrastructure.adapter.out.persistance.repository;

import com.demo.bank.account.domain.enums.Direction;
import com.demo.bank.account.domain.enums.EntryKind;
import com.demo.bank.account.infrastructure.adapter.out.persistance.entity.LedgerEntryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ReactiveLedgerEntryRepository
        extends ReactiveCrudRepository<LedgerEntryEntity, Long> {

    Mono<LedgerEntryEntity> findByTransactionIdAndAccountIdAndDirectionAndEntryKind(
            Long transactionId,
            Long accountId,
            Direction direction,
            EntryKind entryKind
    );
}