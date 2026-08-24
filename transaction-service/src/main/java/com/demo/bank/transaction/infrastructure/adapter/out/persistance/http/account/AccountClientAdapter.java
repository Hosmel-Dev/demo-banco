package com.demo.bank.transaction.infrastructure.adapter.out.persistance.http.account;

import com.demo.bank.transaction.application.dto.command.AccountTransferCommand;
import com.demo.bank.transaction.application.dto.result.AccountTransferResult;
import com.demo.bank.transaction.application.port.out.AccountPort;
import com.demo.bank.transaction.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AccountClientAdapter implements AccountPort {
    private final WebClient accountWebClient;

    @Override
    public Mono<AccountTransferResult> debit(AccountTransferCommand account) {
        AccountRequest request = AccountRequest.builder()
                .id(account.id())
                .amount(account.money().amount())
                .currency(account.money().currency())
                .build();
        return accountWebClient
                .patch()
                .uri("accounts/debit")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AccountResponse.class)
                .map(accountResponse ->
                        new AccountTransferResult(
                                accountResponse.getId(),
                                accountResponse.getBalance()
                        ));
    }

    @Override
    public Mono<AccountTransferResult> credit(AccountTransferCommand account) {
        AccountRequest request = AccountRequest.builder()
                .id(account.id())
                .amount(account.money().amount())
                .currency(account.money().currency())
                .build();
        return accountWebClient
                .patch()
                .uri("accounts/credit")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AccountResponse.class)
                .map(accountResponse ->
                        new AccountTransferResult(
                                accountResponse.getId(),
                                accountResponse.getBalance()
                        ));
    }


}
