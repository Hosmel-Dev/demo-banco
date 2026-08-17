package com.demo.bank.account.infrastructure.adapter.out.persistance.repository;

import com.demo.bank.account.infrastructure.adapter.out.persistance.entity.AccountEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveAccountRepository extends ReactiveCrudRepository<AccountEntity, Long> {
}
