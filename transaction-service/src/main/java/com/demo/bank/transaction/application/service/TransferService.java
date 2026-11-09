package com.demo.bank.transaction.application.service;

import com.demo.bank.transaction.application.dto.command.CreateTransferCommand;
import com.demo.bank.transaction.application.dto.result.CreateTransferResult;
import com.demo.bank.transaction.application.port.in.TransferUseCase;
import com.demo.bank.transaction.application.port.out.AccountPort;
import com.demo.bank.transaction.application.port.out.TransactionRepositoryPortOut;
import com.demo.bank.transaction.domain.enums.EntryKind;
import com.demo.bank.transaction.domain.enums.TransactionStatus;
import com.demo.bank.transaction.domain.enums.TransactionType;
import com.demo.bank.transaction.domain.exception.*;
import com.demo.bank.transaction.domain.model.FinancialTransaction;
import com.demo.bank.transaction.domain.model.Money;
import com.demo.bank.transaction.infrastructure.exception.AccountNotActiveException;
import com.demo.bank.transaction.infrastructure.exception.DifferentCurrencyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransferService implements TransferUseCase {
    private final TransactionRepositoryPortOut transactionRepositoryPortOut;
    private final AccountPort accountPort;

    @Override
    public Mono<CreateTransferResult> execute(CreateTransferCommand command) {
        var money = createMoney(command);
        var originAccountId = command.ledgerEntries().originAccountId();
        var destinationAccountNumber = command.ledgerEntries().destinationAccountNumber();
        var transaction = createFinancialTransaction(command, money);
        var idempotencyKey = command.idempotencyKey();

        return accountPort.findAccountById(originAccountId)
                .then(
                    transactionRepositoryPortOut.findByKey(idempotencyKey)
                            .map(this::returnTransferResult)
                    .switchIfEmpty(
                    accountPort.findAccountIdByNumber(destinationAccountNumber)
                    //.switchIfEmpty(Mono.error(new AccountNotFoundException(destinationAccountNumber)))
                            .flatMap(accountValidationResult-> {
                                if (Objects.equals(originAccountId, accountValidationResult.id())){
                                    return Mono.error(
                                            new SameAccountTransferException(
                                                    originAccountId
                                            ));
                                }
                                if (!accountValidationResult.status().equals("ACTIVE")) {
                                    return Mono.error(
                                            new AccountNotActiveException(
                                                    accountValidationResult.status()
                                            ));
                                }
                                if (!accountValidationResult.currency().equals(money.currency())) {
                                    return Mono.error(
                                            new DifferentCurrencyException(
                                                    accountValidationResult.currency().toString(),
                                                    money.currency().toString()
                                            ));
                                }
                                transaction.setDestinationAccountId(accountValidationResult.id());
                                return transactionRepositoryPortOut.save(transaction)
                                        .flatMap(saved ->
                                                toTransfer(saved,money,originAccountId,accountValidationResult.id())
                                                        .then(markAsSuccessful(saved)).map(
                                                                this::returnTransferResult));
                            }))
                );
    }

    private CreateTransferResult returnTransferResult(
            FinancialTransaction transaction){
        return new CreateTransferResult(
                transaction.getId(),
                transaction.getIdempotencyKey(),
                transaction.getType(),
                transaction.getMoney().amount(),
                transaction.getMoney().currency(),
                transaction.getStatus(),
                transaction.getOriginAccountId(),
                transaction.getDestinationAccountId(),
                transaction.getCreatedAt()
        );
    }

    private Mono<FinancialTransaction> markAsSuccessful(FinancialTransaction transaction){
        return Mono.defer(()->{
            transaction.markAsSuccessful();
            return transactionRepositoryPortOut.save(transaction)
                    .switchIfEmpty(Mono.error(new FailedTransactionException(transaction.getId())));
        });
    }

    private Mono<FinancialTransaction> markAsFailed(FinancialTransaction transaction){
        return Mono.defer(()->{
            transaction.markAsFailed();
            return transactionRepositoryPortOut.save(transaction);
        });
    }

    private Mono<Void> toTransfer(
            FinancialTransaction transaction,
            Money money,
            Long originAccountId,
            Long destinationAccountId) {

        return accountPort
                .debit(
                        originAccountId,
                        money,
                        transaction.getId(),
                        EntryKind.ORIGINAL)
                .onErrorResume(error -> {
                    if (AccountFailureClassifier.definitive(error)) {
                        return markAsFailed(transaction).then(Mono.error(error));
                    }
                    return Mono.error(error);
                })
                .then(Mono.defer(() ->
                        accountPort
                                .credit(
                                        destinationAccountId,
                                        money,
                                        transaction.getId(),
                                        EntryKind.ORIGINAL)
                                .onErrorResume(error -> {
                                    if (!AccountFailureClassifier.definitive(error)) {
                                        return Mono.error(error);
                                    }

                                    return accountPort
                                            .credit(
                                                    originAccountId,
                                                    money,
                                                    transaction.getId(),
                                                    EntryKind.COMPENSATION)
                                            .then(markAsFailed(transaction))
                                            .then(Mono.error(error));
                                })))
                .then();
    }

    private Money createMoney(CreateTransferCommand command){
        return new Money(
                command.transaction().amount(),
                command.transaction().currency()
        );
    }

    private FinancialTransaction createFinancialTransaction(
            CreateTransferCommand command,
            Money money){
        return FinancialTransaction.builder()
                .idempotencyKey(command.idempotencyKey())
                .type(TransactionType.TRANSFER)
                .money(money)
                .status(TransactionStatus.PENDING)
                .description(command.transaction().description())
                .originAccountId(command.ledgerEntries().originAccountId())
                .build();
    }
}
