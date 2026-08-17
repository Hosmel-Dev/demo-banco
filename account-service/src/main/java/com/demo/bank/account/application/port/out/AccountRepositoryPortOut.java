package com.demo.bank.account.application.port.out;

import com.demo.bank.account.domain.model.Account;
import reactor.core.publisher.Mono;

public interface AccountRepositoryPortOut {
    Mono<Account> findById(Long id);
    Mono<Account> save(Account account);
}
