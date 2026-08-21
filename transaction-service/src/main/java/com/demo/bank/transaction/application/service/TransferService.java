package com.demo.bank.transaction.application.service;

import com.demo.bank.transaction.application.dto.command.CreateTransferCommand;
import com.demo.bank.transaction.application.dto.result.CreateTransferResult;
import com.demo.bank.transaction.application.port.in.TransferUseCase;
import com.demo.bank.transaction.application.port.out.TransactionRepositoryPortOut;
import com.demo.bank.transaction.domain.enums.Direction;
import com.demo.bank.transaction.domain.enums.TransactionStatus;
import com.demo.bank.transaction.domain.model.FinancialTransaction;
import com.demo.bank.transaction.domain.model.LedgerEntry;
import com.demo.bank.transaction.domain.model.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TransferService implements TransferUseCase {
    private final TransactionRepositoryPortOut transactionRepositoryPortOut;

    @Override
    public Mono<CreateTransferResult> excecute(CreateTransferCommand createTransactionCommand) {

        Money money = new Money(createTransactionCommand.transaction().amount(), createTransactionCommand.transaction().currency());
        FinancialTransaction financialTransaction = FinancialTransaction.builder()
                .idempotency_key(createTransactionCommand.idempotencyKey())
                .money(money)
                .status(TransactionStatus.PENDING)
                .description(createTransactionCommand.transaction().description())
                .build();

        return transactionRepositoryPortOut.save(financialTransaction).flatMap(transactionStored -> {
            LedgerEntry originAccount = LedgerEntry.builder()
                    .accountId(createTransactionCommand.ledgerEntries().originAccountId())
                    .transactionId(transactionStored.getId())
                    .direction(Direction.DEBIT)
                    .amount(money)
                    .build();
            LedgerEntry destinationAccount = LedgerEntry.builder()
                    .accountId(createTransactionCommand.ledgerEntries().destinationAccountId())
                    .transactionId(transactionStored.getId())
                    .direction(Direction.CREDIT)
                    .amount(money)
                    .build();

            return null;
        });
    }
}
