package com.demo.bank.transaction.infrastructure.adapter.out.persistence.repository;

import com.demo.bank.transaction.infrastructure.adapter.out.persistence.entity.LedgerEntryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReactiveLedgerEntryRepository extends ReactiveCrudRepository<LedgerEntryEntity, Long> {
}
