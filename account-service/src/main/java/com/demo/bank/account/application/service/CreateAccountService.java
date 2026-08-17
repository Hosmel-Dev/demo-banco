package com.demo.bank.account.application.service;

import com.demo.bank.account.application.port.in.CreateAccountUseCase;
import com.demo.bank.account.application.port.out.AccountRepositoryPortOut;
import com.demo.bank.account.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreateAccountService implements CreateAccountUseCase {
    private final AccountRepositoryPortOut accountRepositoryPortOut;

    @Override
    public Mono<Account> createAccount(Account account) {
        return accountRepositoryPortOut.save(account);
    }
}
