package com.demo.bank.account.infrastructure.adapter.out.persistance.repository;

import com.demo.bank.account.infrastructure.adapter.out.persistance.entity.AccountEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface ReactiveAccountRepository extends ReactiveCrudRepository<AccountEntity, Long> {

    Mono<AccountEntity> findByAccountNumber(String accountNumber);

    @Query("UPDATE account SET balance = balance - :amount WHERE id = :id AND balance >= :amount; SELECT * FROM account WHERE id = :id")
    Mono<AccountEntity> debit(Long id, BigDecimal amount);

    @Query("UPDATE account SET balance = balance + :amount WHERE id = :id; SELECT * FROM account WHERE id = :id")
    Mono<AccountEntity> credit(Long id, BigDecimal amount);
}
