package com.demo.bank.transaction.infrastructure.adapter.out.http.account;

import com.demo.bank.transaction.application.dto.result.AccountOperationResult;
import com.demo.bank.transaction.application.dto.result.AccountValidationResult;
import com.demo.bank.transaction.application.port.out.AccountPort;
import com.demo.bank.transaction.domain.model.Money;
import com.demo.bank.transaction.infrastructure.exception.AccountServiceException;
import com.demo.bank.transaction.infrastructure.exception.AccountServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AccountClientAdapter implements AccountPort {
    private final WebClient accountWebClient;

    @Override
    public Mono<AccountOperationResult> debit(Long id, Money money) {
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
                .bodyToMono(AccountTransactionResponse.class)
                .map(accountResponse ->
                        new AccountOperationResult(
                                accountResponse.id(),
                                accountResponse.balance().amount(),
                                accountResponse.balance().currency()
                        ))
                .onErrorResume(
                        WebClientResponseException.NotFound.class,
                        ex -> Mono.empty()
                );
    }

    @Override
    public Mono<AccountOperationResult> credit(Long id, Money money) {
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
                .bodyToMono(AccountTransactionResponse.class)
                .map(accountResponse ->
                        new AccountOperationResult(
                                accountResponse.id(),
                                accountResponse.balance().amount(),
                                accountResponse.balance().currency()
                        ))
                .onErrorResume(
                        WebClientResponseException.class,
                        ex -> Mono.empty()
                );
    }

    @Override
    public Mono<AccountValidationResult> findAccountIdByNumber(String accountNumber) {
        return accountWebClient
                .get()
                .uri("/accounts/number/{account-number}",accountNumber)
                .retrieve()
                .bodyToMono(AccountValidationResponse.class)
                .map(accountValidationResponse->
                        new AccountValidationResult(
                                accountValidationResponse.id(),
                                accountValidationResponse.status(),
                                accountValidationResponse.balance().currency()
                        ))
                .onErrorResume(
                        WebClientResponseException.class,
                        ex -> Mono.empty()
                )
                .onErrorMap(
                        WebClientResponseException.class,
                        ex -> new AccountServiceException()
                )
                .onErrorMap(
                        WebClientRequestException.class,
                        ex->new AccountServiceUnavailableException()
                );
    }

    @Override
    public Mono<AccountValidationResult> findAccountById(Long id) {
        return accountWebClient
                .get()
                .uri("/accounts/{id}", id)
                .retrieve()
                .bodyToMono(AccountValidationResponse.class)
                .map(accountValidationResponse->
                        new AccountValidationResult(
                                accountValidationResponse.id(),
                                accountValidationResponse.status(),
                                accountValidationResponse.balance().currency()
                        ))
                .onErrorResume(
                        WebClientResponseException.class,
                        ex -> Mono.empty()
                )
                .onErrorMap(
                        WebClientResponseException.class,
                        ex -> new AccountServiceException()
                )
                .onErrorMap(
                        WebClientRequestException.class,
                        ex->new AccountServiceUnavailableException()
                );
    }


}
