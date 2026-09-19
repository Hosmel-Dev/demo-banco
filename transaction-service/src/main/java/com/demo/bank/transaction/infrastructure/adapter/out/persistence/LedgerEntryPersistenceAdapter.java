package com.demo.bank.transaction.infrastructure.adapter.out.persistence;

import com.demo.bank.transaction.application.port.out.LedgerEntriesRepositoryPortOut;
import com.demo.bank.transaction.domain.model.LedgerEntry;
import com.demo.bank.transaction.infrastructure.adapter.out.persistence.mapper.LedgerEntryMapper;
import com.demo.bank.transaction.infrastructure.adapter.out.persistence.repository.ReactiveLedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LedgerEntryPersistenceAdapter implements LedgerEntriesRepositoryPortOut {
    private final ReactiveLedgerEntryRepository reactiveLedgerEntryRepository;
    private final LedgerEntryMapper mapper;

    @Override
    public Mono<LedgerEntry> save(LedgerEntry ledgerEntry) {
        return reactiveLedgerEntryRepository.save(mapper.toEntity(ledgerEntry)).map(mapper::toDomain);
    }

    @Override
    public Mono<LedgerEntry> getById(Long id) {
        return null;
    }

    @Override
    public Mono<LedgerEntry> findByTransaction(Long id, String direction) {
        return reactiveLedgerEntryRepository.findByTransactionIdAndDirection(id, direction)
                .map(mapper::toDomain);
    }
}
