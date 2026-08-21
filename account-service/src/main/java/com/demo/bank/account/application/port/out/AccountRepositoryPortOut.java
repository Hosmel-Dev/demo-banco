package com.demo.bank.account.application.port.out;

import com.demo.bank.account.domain.model.Account;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface AccountRepositoryPortOut {
    Mono<Account> findById(Long id);
    Mono<Account> save(Account account);
    Mono<Account> findByAccountNumber(String number);
    Mono<Account> debit(Long id, BigDecimal amount);
    Mono<Account> credit(Long id, BigDecimal amount);
}
