package com.demo.bank.transaction.application.service;

import com.demo.bank.transaction.application.dto.command.CreateTransferCommand;
import com.demo.bank.transaction.application.dto.result.CreateTransferResult;
import com.demo.bank.transaction.application.port.in.TransferUseCase;
import com.demo.bank.transaction.application.port.out.LedgerEntriesRepositoryPortOut;
import com.demo.bank.transaction.application.port.out.TransactionRepositoryPortOut;
import com.demo.bank.transaction.domain.enums.AppCurrency;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferService implements TransferUseCase {
    private final TransactionRepositoryPortOut transactionRepositoryPortOut;
    private final LedgerEntriesRepositoryPortOut ledgerEntriesRepositoryPortOut;

    @Transactional
    @Override
    public Mono<CreateTransferResult> execute(CreateTransferCommand createTransactionCommand) {

        Money money = new Money(createTransactionCommand.transaction().amount(), createTransactionCommand.transaction().currency());
        FinancialTransaction financialTransaction = FinancialTransaction.builder()
                .idempotencyKey(createTransactionCommand.idempotencyKey())
                .type(TransactionType.TRANSFER)
                .money(money)
                .status(TransactionStatus.PENDING)
                .description(createTransactionCommand.transaction().description())
                .build();
        return transactionRepositoryPortOut.save(financialTransaction).flatMap(transactionStored -> {

            //VALIDAR CUENTA DE ORIGEN A TRAVÉS DE UNA API
            //REALIZAR PETICIONES DE DEBITO Y CRÉDITO
            LedgerEntry originAccount = LedgerEntry.builder()
                    .accountId(createTransactionCommand.ledgerEntries().originAccountId())
                    .transactionId(transactionStored.getId())
                    .direction(Direction.DEBIT)
                    .money(money)
                    .build();
            LedgerEntry destinationAccount = LedgerEntry.builder()
                    .accountId(createTransactionCommand.ledgerEntries().destinationAccountId())
                    .transactionId(transactionStored.getId())
                    .direction(Direction.CREDIT)
                    .money(money)
                    .build();

            return null;
        });
    }
}
