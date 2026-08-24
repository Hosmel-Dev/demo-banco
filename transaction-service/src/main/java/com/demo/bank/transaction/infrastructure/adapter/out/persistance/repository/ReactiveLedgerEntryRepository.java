package com.demo.bank.transaction.infrastructure.adapter.out.persistance.repository;

import com.demo.bank.transaction.infrastructure.adapter.out.persistance.entity.LedgerEntryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReactiveLedgerEntryRepository extends ReactiveCrudRepository<LedgerEntryEntity, Long> {
}
