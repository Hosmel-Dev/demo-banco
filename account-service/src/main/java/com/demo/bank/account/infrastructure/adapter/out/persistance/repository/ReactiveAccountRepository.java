package com.demo.bank.account.infrastructure.adapter.out.persistance.repository;

import com.demo.bank.account.infrastructure.adapter.out.persistance.entity.AccountEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface ReactiveAccountRepository extends ReactiveCrudRepository<AccountEntity, Long> {

    Mono<AccountEntity> findByAccountNumber(String accountNumber);

    Flux<AccountEntity> findByOwnerUserId(Long ownerUserId);

    @Query("SELECT * FROM account WHERE id = :id FOR UPDATE")
    Mono<AccountEntity> findByIdForUpdate(@Param("id") Long id);

    @Modifying
    @Query("""
            UPDATE account
            SET balance = balance - :amount
            WHERE id = :id AND balance >= :amount
            """)
    Mono<Integer> debitBalance(
            @Param("id") Long id,
            @Param("amount") BigDecimal amount);

    @Modifying
    @Query("""
            UPDATE account
            SET balance = balance + :amount
            WHERE id = :id
            """)
    Mono<Integer> creditBalance(
            @Param("id") Long id,
            @Param("amount") BigDecimal amount);
}
