package com.demo.bank.transaction.application.service;

import com.demo.bank.transaction.application.dto.command.CreateTransferCommand;
import com.demo.bank.transaction.application.dto.result.CreateTransferResult;
import com.demo.bank.transaction.application.port.in.TransferUseCase;
import com.demo.bank.transaction.application.port.out.AccountPort;
import com.demo.bank.transaction.application.port.out.LedgerEntriesRepositoryPortOut;
import com.demo.bank.transaction.application.port.out.TransactionRepositoryPortOut;
import com.demo.bank.transaction.domain.enums.Direction;
import com.demo.bank.transaction.domain.enums.TransactionStatus;
import com.demo.bank.transaction.domain.enums.TransactionType;
import com.demo.bank.transaction.domain.exception.*;
import com.demo.bank.transaction.domain.model.FinancialTransaction;
import com.demo.bank.transaction.domain.model.LedgerEntry;
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
    private final LedgerEntriesRepositoryPortOut ledgerEntriesRepositoryPortOut;
    private final AccountPort accountPort;

    @Override
    public Mono<CreateTransferResult> execute(CreateTransferCommand command) {
        var money = createMoney(command);
        var originAccountId = command.ledgerEntries().originAccountId();
        var destinationAccountNumber = command.ledgerEntries().destinationAccountNumber();
        var transaction = createFinancialTransaction(command, money);
        var idempotencyKey = command.idempotencyKey();

        return transactionRepositoryPortOut.findByKey(idempotencyKey)
                .flatMap(response -> {
                    if (response.getStatus().equals(TransactionStatus.PENDING)){
                        return Mono.error(new FailedTransactionException(response.getId()));
                    }

//                    if (response.getType().equals(TransactionType.TRANSFER) || response.getStatus().equals(TransactionStatus.FAILED)){
//                        return searchDestinationAccountId(response.getId())
//                                .map(destinationAccountId ->
//                                        returnTransferResult(response, originAccountId, destinationAccountId));
//                    }
                    return Mono.just(returnTransferResult(response, originAccountId, null));
                })
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
                            return transactionRepositoryPortOut.save(transaction)
                                    .flatMap(saved ->
                                            toTransfer(saved,money,originAccountId,accountValidationResult.id())
                                                    .then(markAsSuccessful(saved)).map(
                                                            successful ->
                                                                    returnTransferResult(
                                                                            successful,
                                                                            originAccountId,
                                                                            accountValidationResult.id()
                                                                    )));
                        }));
    }

//    private Mono<Long> searchDestinationAccountId(Long transactionId){
//        var direction = String.valueOf(Direction.CREDIT);
//        return ledgerEntriesRepositoryPortOut.findByTransaction(transactionId, direction)
//                .map(LedgerEntry::getAccountId);
//    }

    private CreateTransferResult returnTransferResult(
            FinancialTransaction transaction,
            Long originAccountId,
            Long destinationAccountId){
        return new CreateTransferResult(
                transaction.getId(),
                transaction.getIdempotencyKey(),
                transaction.getType(),
                transaction.getMoney().amount(),
                transaction.getMoney().currency(),
                transaction.getStatus(),
                originAccountId,
                destinationAccountId,
                transaction.getCreatedAt()
        );
    }

    private Mono<FinancialTransaction> markAsSuccessful(FinancialTransaction transaction){
        return Mono.defer(()->{
            transaction.markAsSuccessful();
            return transactionRepositoryPortOut.save(transaction);
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
            Long destinationAccountId){
        var originEntry = createLedgerEntry(
                transaction,
                money,
                Direction.DEBIT,
                originAccountId
        );
        var destinationEntry = createLedgerEntry(
                transaction,
                money,
                Direction.CREDIT,
                destinationAccountId
        );

        return
                accountPort.debit(originAccountId,money)
                        .onErrorResume(error ->
                                markAsFailed(transaction)
                                        .then(Mono.error(error)))
                .then(ledgerEntriesRepositoryPortOut.save(originEntry)
                        .onErrorResume(error ->
                                compensationDebit(originEntry, transaction)
                                        .then(Mono.error(error))))
                .then(accountPort.credit(destinationAccountId,money)
                        .onErrorResume(error ->
                                compensationDebitLedger(originEntry, transaction)
                                        .then(Mono.error(error))))
                .then(ledgerEntriesRepositoryPortOut.save(destinationEntry)
                        .onErrorResume(error ->
                                compensationCredit(originEntry, destinationEntry, transaction)
                                        .then(Mono.error(error))))
                .then();
    }

    private Mono<Void> compensationDebit(LedgerEntry originEntry, FinancialTransaction transaction){
        return accountPort.credit(originEntry.getAccountId() ,originEntry.getMoney())
                .then(markAsFailed(transaction))
                .then();
    }

    ///Al fallar los ledgers es contraproducente usar ledgers nuevamente para el registro de la compensación se empleará un servicio adicional
    private Mono<Void> compensationDebitLedger(LedgerEntry originEntry, FinancialTransaction transaction){
        var originEntryCompensation = createLedgerEntryCompensation(originEntry);

        return accountPort.credit(originEntryCompensation.getAccountId() ,originEntryCompensation.getMoney())
                .then(ledgerEntriesRepositoryPortOut.save(originEntryCompensation))
                .then(markAsFailed(transaction))
                .then();
    }

    private Mono<Void> compensationCredit(
            LedgerEntry originEntry,
            LedgerEntry destinationEntry,
            FinancialTransaction transaction
    ){
        var originEntryCompensation = createLedgerEntryCompensation(originEntry);
        var destinationEntryCompensation = createLedgerEntryCompensation(destinationEntry);

        return accountPort.debit(destinationEntryCompensation.getAccountId() ,originEntryCompensation.getMoney())
                .then(accountPort.credit(originEntryCompensation.getAccountId() ,originEntryCompensation.getMoney()))
                //.then(ledgerEntriesRepositoryPortOut.save(originEntryCompensation))
                //.then(ledgerEntriesRepositoryPortOut.save(destinationEntryCompensation))
                .then(markAsFailed(transaction))
                .then();
    }
    ///

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
                .build();
    }

    private LedgerEntry createLedgerEntry(
            FinancialTransaction transaction,
            Money money,
            Direction direction,
            Long accountId){

        return LedgerEntry.builder()
                .accountId(accountId)
                .transactionId(transaction.getId())
                .direction(direction)
                .money(money)
                .build();
    }

    private LedgerEntry createLedgerEntryCompensation(
            LedgerEntry original){

        Direction compensationDirection =
                original.getDirection() == Direction.DEBIT
                        ? Direction.CREDIT
                        : Direction.DEBIT;

        return LedgerEntry.builder()
                .transactionId(original.getTransactionId())
                .accountId(original.getAccountId())
                .money(original.getMoney())
                .direction(compensationDirection)
                .build();
    }
}
