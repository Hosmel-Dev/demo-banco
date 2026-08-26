package com.demo.bank.transaction.application.service;


import com.demo.bank.transaction.application.dto.command.CreateTransactionCommand;
import com.demo.bank.transaction.application.dto.result.CreateTransactionResult;
import com.demo.bank.transaction.application.port.in.DebitUseCase;
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
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DebitService implements DebitUseCase {
    private final TransactionRepositoryPortOut transactionRepositoryPortOut;
    private final AccountPort accountPort;
    private final LedgerEntriesRepositoryPortOut ledgerEntriesRepositoryPortOut;

    @Override
    public Mono<CreateTransactionResult> execute(CreateTransactionCommand command) {
        Money money = new Money(
                command.transaction().amount(),
                command.transaction().currency()
        );

        FinancialTransaction financialTransaction = FinancialTransaction.builder()
                .idempotencyKey(command.idempotencyKey())
                .type(TransactionType.WITHDRAWAL)
                .money(money)
                .status(TransactionStatus.PENDING)
                .description(command.transaction().description())
                .build();

        return transactionRepositoryPortOut.save(financialTransaction).flatMap(savedTransaction -> {
            LedgerEntry accountEntry = LedgerEntry.builder()
                    .transactionId(savedTransaction.getId())
                    .accountId(command.ledgerEntries().accountId())
                    .direction(Direction.DEBIT)
                    .money(money)
                    .build();

            return accountPort.debit(command.ledgerEntries().accountId(),
                    money)
                    .then(ledgerEntriesRepositoryPortOut.save(accountEntry))
                    .then(Mono.defer(()->{
                        savedTransaction.setStatus(TransactionStatus.SUCCESS);
                        savedTransaction.setCreatedAt(LocalDateTime.now());
                        return transactionRepositoryPortOut.save(savedTransaction);
                    }))
                    .map(successfulTransaction -> new CreateTransactionResult(
                            successfulTransaction.getId(),
                            successfulTransaction.getIdempotencyKey(),
                            successfulTransaction.getType(),
                            successfulTransaction.getMoney().amount(),
                            successfulTransaction.getMoney().currency(),
                            successfulTransaction.getStatus(),
                            command.ledgerEntries().accountId(),
                            successfulTransaction.getCreatedAt()
                    ));
        });
    }
}
