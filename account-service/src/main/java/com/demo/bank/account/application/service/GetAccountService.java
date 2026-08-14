package com.demo.bank.account.application.service;

import com.demo.bank.account.application.port.in.GetAccountUseCase;
import com.demo.bank.account.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetAccountService implements GetAccountUseCase {

    @Override
    public Mono<Account> getAccount(Long id) {
        return null;
    }
}
