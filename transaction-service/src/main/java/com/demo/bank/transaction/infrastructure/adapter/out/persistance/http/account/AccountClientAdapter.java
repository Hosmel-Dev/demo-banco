package com.demo.bank.transaction.infrastructure.adapter.out.persistance.http.account;

import com.demo.bank.transaction.application.dto.command.AccountTransferCommand;
import com.demo.bank.transaction.application.dto.result.AccountTransferResult;
import com.demo.bank.transaction.application.port.out.AccountPort;
import com.demo.bank.transaction.domain.model.Account;
import com.demo.bank.transaction.domain.model.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AccountClientAdapter implements AccountPort {
    private final WebClient accountWebClient;

    @Override
    public Mono<AccountTransferResult> debit(Long id, Money money) {
        AccountRequest request = AccountRequest.builder()
                .id(id)
                .amount(money.amount())
                .currency(money.currency())
                .build();
        return accountWebClient
                .post()
                .uri("/accounts/debit")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AccountResponse.class)
                .map(accountResponse ->
                        new AccountTransferResult(
                                accountResponse.id(),
                                accountResponse.balance().amount(),
                                accountResponse.balance().currency()
                        ));
    }

    @Override
    public Mono<AccountTransferResult> credit(Long id, Money money) {
        AccountRequest request = AccountRequest.builder()
                .id(id)
                .amount(money.amount())
                .currency(money.currency())
                .build();
        return accountWebClient
                .post()
                .uri("/accounts/credit")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AccountResponse.class)
                .map(accountResponse ->
                        new AccountTransferResult(
                                accountResponse.id(),
                                accountResponse.balance().amount(),
                                accountResponse.balance().currency()
                        ));
    }

    @Override
    public Mono<Long> validate(String accountNumber) {
        return accountWebClient
                .get()
                .uri("/accounts/number/{account-number}",accountNumber)
                .retrieve()
                .bodyToMono(ValidationAccountResponse.class)
                .map(accountResponse -> {
                    if (accountResponse == null || !accountResponse.status().equals("ACTIVE")){
                        return null;
                    }
                    return accountResponse.id();
                });
    }


}
