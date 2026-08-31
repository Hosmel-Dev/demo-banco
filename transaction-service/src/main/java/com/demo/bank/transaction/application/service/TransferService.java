package com.demo.bank.transaction.application.service;

import com.demo.bank.transaction.application.dto.command.CreateTransactionCommand;
import com.demo.bank.transaction.application.dto.command.CreateTransferCommand;
import com.demo.bank.transaction.application.dto.result.CreateTransferResult;
import com.demo.bank.transaction.application.port.in.TransferUseCase;
import com.demo.bank.transaction.application.port.out.AccountPort;
import com.demo.bank.transaction.application.port.out.LedgerEntriesRepositoryPortOut;
import com.demo.bank.transaction.application.port.out.TransactionRepositoryPortOut;
import com.demo.bank.transaction.domain.enums.Direction;
import com.demo.bank.transaction.domain.enums.TransactionStatus;
import com.demo.bank.transaction.domain.enums.TransactionType;
import com.demo.bank.transaction.domain.model.FinancialTransaction;
import com.demo.bank.transaction.domain.model.LedgerEntry;
import com.demo.bank.transaction.domain.model.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferService implements TransferUseCase {
    private final TransactionRepositoryPortOut transactionRepositoryPortOut;
    private final LedgerEntriesRepositoryPortOut ledgerEntriesRepositoryPortOut;
    private final AccountPort accountPort;

    //FALTARÍA GESTIONAR CORRECTAMENTE EL TRANSACTIONAL PARA LA TRAZABILIDAD DE INTENTOS
    //TRANSACCIÓN A UNO MISMO XD
    @Override
    public Mono<CreateTransferResult> execute(CreateTransferCommand command) {
        var money = createMoney(command);
        var originAccountId = command.ledgerEntries().originAccountId();
        var transaction = createFinancialTransaction(command, money);

        return transactionRepositoryPortOut.save(transaction)
                .flatMap(saved ->
                        accountPort.validate(command.ledgerEntries().destinationAccountNumber())
                                .flatMap(destinationAccountId ->
                                    toTransfer(saved,money,originAccountId,destinationAccountId)
                                            .then(markAsSuccessful(saved))
                                            .map(successful->
                                                    returnTransferResult(
                                                            successful,
                                                            originAccountId,
                                                            destinationAccountId))
                                )
                );
    }

    private CreateTransferResult returnTransferResult(FinancialTransaction transaction, Long originAccountId, Long destinationAccountId){
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
        transaction.markAsSuccessful();
        return Mono.defer(()->{
                    transaction.markAsSuccessful();
                    return transactionRepositoryPortOut.save(transaction);
                });
    }

    private Mono<Void> toTransfer(FinancialTransaction transaction, Money money, Long originAccountId, Long destinationAccountId){
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

        return accountPort.debit(originAccountId,money)
                .then(accountPort.credit(destinationAccountId,money))
                .then(ledgerEntriesRepositoryPortOut.save(originEntry))
                .then(ledgerEntriesRepositoryPortOut.save(destinationEntry))
                .then();
    }

    private Money createMoney(CreateTransferCommand command){
        return new Money(
                command.transaction().amount(),
                command.transaction().currency()
        );
    }

    private FinancialTransaction createFinancialTransaction(CreateTransferCommand command, Money money){
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
}
