package com.demo.bank.account.application.port.in;

import com.demo.bank.account.domain.model.Account;
import reactor.core.publisher.Mono;

public interface GetAccountByNumberUseCase {
    Mono<Account> execute(String accountNumber);
}
