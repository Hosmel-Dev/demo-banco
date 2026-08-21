package com.demo.bank.account.application.service;

import com.demo.bank.account.application.port.in.GetAccountByNumberUseCase;
import com.demo.bank.account.application.port.out.AccountRepositoryPortOut;
import com.demo.bank.account.domain.model.Account;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class GetAccountByNumberService implements GetAccountByNumberUseCase {

    private final AccountRepositoryPortOut accountRepositoryPortOut;

    @Override
    public Mono<Account> execute(String accountNumber) {
        return accountRepositoryPortOut.findByAccountNumber(accountNumber);
    }
}
