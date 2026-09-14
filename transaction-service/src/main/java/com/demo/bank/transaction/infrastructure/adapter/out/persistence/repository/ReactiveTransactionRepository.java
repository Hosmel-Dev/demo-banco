package com.demo.bank.transaction.infrastructure.adapter.out.persistence.repository;

import com.demo.bank.transaction.infrastructure.adapter.out.persistence.entity.FinancialTransactionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveTransactionRepository extends ReactiveCrudRepository<FinancialTransactionEntity, Long> {
}
