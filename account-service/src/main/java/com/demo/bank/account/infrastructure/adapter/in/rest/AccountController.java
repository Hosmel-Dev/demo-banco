package com.demo.bank.account.infrastructure.adapter.in.rest;

import com.demo.bank.account.application.port.in.GetAccountUseCase;
import com.demo.bank.account.domain.model.Account;
import com.demo.bank.account.infrastructure.adapter.in.rest.response.AccountResponse;
import com.demo.bank.account.infrastructure.adapter.out.persistance.mapper.AccountMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/accounts")
public class AccountController {
    private final GetAccountUseCase getAccountsUseCase;
    private final AccountMapper mapper;

    @GetMapping("/{id}")
    public Mono<AccountResponse> getAccount(@NonNull @PathVariable Long id){
        return getAccountsUseCase.getAccount(id).map(mapper::toResponse);
    }

}
