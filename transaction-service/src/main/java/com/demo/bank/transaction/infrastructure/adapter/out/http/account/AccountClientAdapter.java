package com.demo.bank.transaction.infrastructure.adapter.out.http.account;

import com.demo.bank.transaction.application.dto.result.AccountOperationResult;
import com.demo.bank.transaction.application.dto.result.AccountValidationResult;
import com.demo.bank.transaction.application.port.out.AccountPort;
import com.demo.bank.transaction.domain.enums.EntryKind;
import com.demo.bank.transaction.infrastructure.exception.AccountNotFoundException;
import com.demo.bank.transaction.domain.model.Money;
import com.demo.bank.transaction.infrastructure.exception.*;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

@Component
public class AccountClientAdapter implements AccountPort {
    private final WebClient accountWebClient;
    private final String internalDebitKey;

    public AccountClientAdapter(
            WebClient accountWebClient,
            @Value("${bank.internal.debit.key}") String internalDebitKey) {
        this.accountWebClient = accountWebClient;
        this.internalDebitKey = internalDebitKey;
    }

    @CircuitBreaker(name = "accountMovement", fallbackMethod = "debitFallback")
    @Override
    public Mono<AccountOperationResult> debit(Long accountId, Money money, Long transactionId, EntryKind entryKind) {
        AccountRequest request = AccountRequest.builder()
                .id(accountId)
                .amount(money.amount())
                .currency(money.currency())
                .transactionId(transactionId)
                .entryKind(entryKind)
                .build();
        return accountWebClient
                .post()
                .uri("/accounts/debit")
                .header("X-Internal-Service-Key", internalDebitKey)
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
                .switchIfEmpty(Mono.error(new AccountServiceException()))
                .onErrorMap(
                        WebClientRequestException.class,
                        ex -> new AccountServiceUnavailableException()
                );
    }

    private Mono<AccountOperationResult> debitFallback(
            Long accountId,
            Money money,
            Long transactionId,
            EntryKind entryKind,
            CallNotPermittedException exception) {
        return Mono.error(new AccountServiceUnavailableException());
    }

    @CircuitBreaker(name = "accountMovement", fallbackMethod = "creditFallback")
    @Override
    public Mono<AccountOperationResult> credit(Long accountId, Money money, Long transactionId, EntryKind entryKind) {
        AccountRequest request = AccountRequest.builder()
                .id(accountId)
                .amount(money.amount())
                .currency(money.currency())
                .transactionId(transactionId)
                .entryKind(entryKind)
                .build();
        return accountWebClient
                .post()
                .uri("/accounts/credit")
                .header("X-Internal-Service-Key", internalDebitKey)
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
                .switchIfEmpty(Mono.error(new AccountServiceException()))
                .onErrorMap(
                        WebClientRequestException.class,
                        ex -> new AccountServiceUnavailableException()
                );
    }

    private Mono<AccountOperationResult> creditFallback(
            Long accountId,
            Money money,
            Long transactionId,
            EntryKind entryKind,
            CallNotPermittedException exception) {
        return Mono.error(new AccountServiceUnavailableException());
    }

    @Override
    @CircuitBreaker(name = "accountLookup", fallbackMethod = "accountByNumberFallback")
    public Mono<AccountValidationResult> findAccountIdByNumber(String accountNumber) {
        return accountWebClient
                .get()
                .uri("/accounts/number/{account-number}",accountNumber)
                .header("X-Internal-Service-Key", internalDebitKey)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> response
                                .bodyToMono(AccountServiceProblem.class)
                                .map(problem -> mapClientError(accountNumber,problem))
                                .switchIfEmpty(Mono.fromSupplier(() ->
                                        response.statusCode().value() == 404
                                                ? new AccountNotFoundException(accountNumber)
                                                : new AccountServiceException()))
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
                                accountValidationResponse.balance().currency(),
                                accountValidationResponse.ownerUserId()
                        ))
                .switchIfEmpty(Mono.error(new AccountServiceException()))
                .onErrorMap(
                        WebClientRequestException.class,
                        ex -> new AccountServiceUnavailableException()
                );
    }

    private Mono<AccountValidationResult> accountByNumberFallback(
            String accountNumber,
            CallNotPermittedException exception) {
        return Mono.error(new AccountServiceUnavailableException());
    }

    @Override
    @CircuitBreaker(name = "accountLookup", fallbackMethod = "accountByIdFallback")
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
                                .switchIfEmpty(Mono.fromSupplier(() ->
                                        response.statusCode().value() == 404
                                                ? new AccountNotFoundException(accountId)
                                                : new AccountServiceException()))
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
                                accountValidationResponse.balance().currency(),
                                accountValidationResponse.ownerUserId()
                        ))
                .switchIfEmpty(Mono.error(new AccountServiceException()))
                .onErrorMap(
                        WebClientRequestException.class,
                        ex -> new AccountServiceUnavailableException()
                );
    }

    private Mono<AccountValidationResult> accountByIdFallback(
            Long accountId,
            CallNotPermittedException exception) {
        return Mono.error(new AccountServiceUnavailableException());
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
            case "FORBIDDEN_ACCESS" -> new ForbiddenAccessException();
            case "LEDGER_CONFLICT" -> new AccountLedgerConflictException();
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
