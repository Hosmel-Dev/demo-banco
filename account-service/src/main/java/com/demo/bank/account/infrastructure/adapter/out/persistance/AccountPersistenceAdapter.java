package com.demo.bank.account.infrastructure.adapter.out.persistance;

import com.demo.bank.account.application.port.out.AccountRepositoryPortOut;
import com.demo.bank.account.domain.model.Account;
import com.demo.bank.account.infrastructure.adapter.out.persistance.entity.AccountEntity;
import com.demo.bank.account.infrastructure.adapter.out.persistance.mapper.AccountPersistenceMapper;
import com.demo.bank.account.infrastructure.adapter.out.persistance.repository.ReactiveAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements AccountRepositoryPortOut {
    private final ReactiveAccountRepository reactiveAccountRepository;
    private final AccountPersistenceMapper mapper;

    @Override
    public Mono<Account> findById(Long id) {
        Mono<AccountEntity> entity = reactiveAccountRepository.findById(id);
        return entity.map(mapper::entityToDomain);
    }

    @Override
    public Mono<Account> save(Account account) {
        Mono<AccountEntity> entity = reactiveAccountRepository.save(mapper.toEntity(account));
        return entity.map(mapper::entityToDomain);
    }

    @Override
    public Mono<Account> findByAccountNumber(String number) {
        Mono<AccountEntity> entity = reactiveAccountRepository.findByAccountNumber(number);
        return entity.map(mapper::entityToDomain);
    }

    @Override
    public Mono<Account> debit(Long id, BigDecimal amount) {
        return reactiveAccountRepository.debit(id, amount).map(mapper::entityToDomain);
    }

    @Override
    public Mono<Account> credit(Long id, BigDecimal amount) {
        return reactiveAccountRepository.credit(id, amount).map(mapper::entityToDomain);
    }
}
