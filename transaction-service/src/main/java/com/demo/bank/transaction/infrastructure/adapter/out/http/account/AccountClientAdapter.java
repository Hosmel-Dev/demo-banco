package com.demo.bank.transaction.infrastructure.adapter.out.http.account;

import com.demo.bank.transaction.application.dto.result.AccountOperationResult;
import com.demo.bank.transaction.application.dto.result.AccountValidationResult;
import com.demo.bank.transaction.application.port.out.AccountPort;
import com.demo.bank.transaction.infrastructure.exception.AccountNotFoundException;
import com.demo.bank.transaction.domain.model.Money;
import com.demo.bank.transaction.infrastructure.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
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
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> response
                                .bodyToMono(AccountServiceProblem.class)
                                .map(problem -> mapClientError(id,problem))
                                .switchIfEmpty(
                                        Mono.just(new AccountServiceException())
                                )
                )
                .bodyToMono(AccountTransactionResponse.class)
                .map(accountResponse ->
                        new AccountOperationResult(
                                accountResponse.id(),
                                accountResponse.balance().amount(),
                                accountResponse.balance().currency()
                        ))
                .onErrorMap(
                        WebClientRequestException.class,
                        ex -> new AccountServiceUnavailableException()
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
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> response
                                .bodyToMono(AccountServiceProblem.class)
                                .map(problem -> mapClientError(id,problem))
                                .switchIfEmpty(
                                        Mono.just(new AccountServiceException())
                                )
                )
                .bodyToMono(AccountTransactionResponse.class)
                .map(accountResponse ->
                        new AccountOperationResult(
                                accountResponse.id(),
                                accountResponse.balance().amount(),
                                accountResponse.balance().currency()
                        ))
                .onErrorMap(
                        WebClientRequestException.class,
                        ex -> new AccountServiceUnavailableException()
                );
    }

    @Override
    public Mono<AccountValidationResult> findAccountIdByNumber(String accountNumber) {
        return accountWebClient
                .get()
                .uri("/accounts/number/{account-number}",accountNumber)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> response
                                .bodyToMono(AccountServiceProblem.class)
                                .map(problem -> mapClientError(accountNumber,problem))
                )
                .bodyToMono(AccountValidationResponse.class)
                .map(accountValidationResponse->
                        new AccountValidationResult(
                                accountValidationResponse.id(),
                                accountValidationResponse.status(),
                                accountValidationResponse.balance().currency()
                        ))
                .onErrorMap(
                        WebClientRequestException.class,
                        ex -> new AccountServiceUnavailableException()
                );
    }

    @Override
    public Mono<AccountValidationResult> findAccountById(Long id) {
        return accountWebClient
                .get()
                .uri("/accounts/{id}", id)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> response
                                .bodyToMono(AccountServiceProblem.class)
                                .map(problem -> mapClientError(id,problem))
                                .switchIfEmpty(
                                        Mono.just(new AccountServiceException())
                                )
                )
                .bodyToMono(AccountValidationResponse.class)
                .map(accountValidationResponse->
                        new AccountValidationResult(
                                accountValidationResponse.id(),
                                accountValidationResponse.status(),
                                accountValidationResponse.balance().currency()
                        ))
                .onErrorMap(
                        WebClientRequestException.class,
                        ex -> new AccountServiceUnavailableException()
                );
    }

    private RuntimeException mapClientError(Long id, AccountServiceProblem problemDetail){
        String code = problemDetail.code() != null
                ? problemDetail.code()
                : "";

        return switch (code) {
            case "ACCOUNT_NOT_FOUND" -> new AccountNotFoundException(id);
            case "ACCOUNT_NOT_ACTIVE" -> new AccountNotActiveException(id);
            case "WRONG_CURRENCY" -> new DifferentCurrencyException();
            case "INSUFFICIENT_FUNDS" -> new InsufficientFundsException();
            default -> new AccountServiceException();
        };

    }

    private RuntimeException mapClientError(String  id, AccountServiceProblem problemDetail){
        String code = problemDetail.code() != null
                ? problemDetail.code()
                : "";

        return switch (code) {
            case "ACCOUNT_NOT_FOUND" -> new AccountNotFoundException(id);
            case "ACCOUNT_NOT_ACTIVE" -> new AccountNotActiveException(id);
            case "WRONG_CURRENCY" -> new DifferentCurrencyException();
            case "INSUFFICIENT_FUNDS" -> new InsufficientFundsException();
            default -> new AccountServiceException();
        };

    }


}
