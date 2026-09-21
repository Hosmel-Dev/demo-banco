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
    public Mono<AccountOperationResult> debit(Long accountId, Money money) {
        AccountRequest request = AccountRequest.builder()
                .id(accountId)
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
                                .map(problem -> mapClientError(accountId,problem))
                                .switchIfEmpty(
                                        Mono.just(new AccountServiceException())
                                )
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.just(
                                new AccountServiceUnavailableException()
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
    public Mono<AccountOperationResult> credit(Long accountId, Money money) {
        AccountRequest request = AccountRequest.builder()
                .id(accountId)
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
                                .map(problem -> mapClientError(accountId,problem))
                                .switchIfEmpty(
                                        Mono.just(new AccountServiceException())
                                )
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.just(
                                new AccountServiceUnavailableException()
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
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.just(
                                new AccountServiceUnavailableException()
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

    @Override
    public Mono<AccountValidationResult> findAccountById(Long accountId) {
        return accountWebClient
                .get()
                .uri("/accounts/{id}", accountId)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> response
                                .bodyToMono(AccountServiceProblem.class)
                                .map(problem -> mapClientError(accountId,problem))
                                .switchIfEmpty(
                                        Mono.just(new AccountServiceException())
                                )
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.just(
                                new AccountServiceUnavailableException()
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

    private RuntimeException mapClientError(Long accountId, AccountServiceProblem problemDetail){
        String code = problemDetail.code() != null
                ? problemDetail.code()
                : "";

        return switch (code) {
            case "ACCOUNT_NOT_FOUND" -> new AccountNotFoundException(accountId);
            case "ACCOUNT_NOT_ACTIVE" -> new AccountNotActiveException(accountId);
            case "WRONG_CURRENCY" -> new DifferentCurrencyException();
            case "INSUFFICIENT_FUNDS" -> new InsufficientFundsException(accountId);
            default -> new AccountServiceException();
        };

    }

    private RuntimeException mapClientError(String  accountNumber, AccountServiceProblem problemDetail){
        String code = problemDetail.code() != null
                ? problemDetail.code()
                : "";

        if (code.equals("ACCOUNT_NOT_FOUND")) {
            return new AccountNotFoundException(accountNumber);
        }
        else {
            return new AccountServiceException();
        }
    }

}
