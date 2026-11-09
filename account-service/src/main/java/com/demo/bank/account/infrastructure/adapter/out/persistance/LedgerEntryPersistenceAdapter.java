package com.demo.bank.account.infrastructure.adapter.out.persistance;

import com.demo.bank.account.application.port.out.LedgerEntryPortOut;
import com.demo.bank.account.domain.enums.Direction;
import com.demo.bank.account.domain.enums.EntryKind;
import com.demo.bank.account.domain.model.LedgerEntry;
import com.demo.bank.account.infrastructure.adapter.out.persistance.entity.LedgerEntryEntity;

import com.demo.bank.account.infrastructure.adapter.out.persistance.mapper.LedgerEntryMapper;
import com.demo.bank.account.infrastructure.adapter.out.persistance.repository.ReactiveLedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LedgerEntryPersistenceAdapter implements LedgerEntryPortOut {
    private final ReactiveLedgerEntryRepository repository;
    private final LedgerEntryMapper mapper;

    @Override
    public Mono<LedgerEntry> find(Long transactionId, Long accountId, Direction direction, EntryKind entryKind) {
        return repository.findByTransactionIdAndAccountIdAndDirectionAndEntryKind(
                        transactionId,
                        accountId,
                        direction,
                        entryKind)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<LedgerEntry> save(LedgerEntry entry) {
        LedgerEntryEntity entity = mapper.toEntity(entry);
        return repository.save(entity).map(mapper::toDomain);
    }
}