package com.demo.bank.account.application.service;

import com.demo.bank.account.application.port.in.GetAccountUseCase;
import com.demo.bank.account.application.port.out.AccountRepositoryPortOut;
import com.demo.bank.account.domain.exception.AccountNotFoundException;
import com.demo.bank.account.domain.exception.ForbiddenAccessException;
import com.demo.bank.account.domain.exception.UnauthorizedUserException;
import com.demo.bank.account.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GetAccountService implements GetAccountUseCase {
    private final AccountRepositoryPortOut repositoryPortOut;

    @Override
    public Mono<Account> getAccount(Long id, Long requesterUserId) {
        return repositoryPortOut.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(id)))
                .flatMap(account -> {
                    if(!Objects.equals(account.getOwnerUserId(), requesterUserId)){
                        return Mono.error(new ForbiddenAccessException());
                    }
                    return Mono.just(account);
                });
    }

    @Override
    public Flux<Account> findByOwnerUserId(Long requesterUserId) {
        return repositoryPortOut.findByOwnerUserId(requesterUserId);
    }
}
