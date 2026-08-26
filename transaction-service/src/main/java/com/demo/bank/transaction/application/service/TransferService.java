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
    @Transactional
    @Override
    public Mono<CreateTransferResult> execute(CreateTransferCommand command) {
        Money money = new Money(
                command.transaction().amount(),
                command.transaction().currency()
        );

        FinancialTransaction financialTransaction = FinancialTransaction.builder()
                .idempotencyKey(command.idempotencyKey())
                .type(TransactionType.TRANSFER)
                .money(money)
                .status(TransactionStatus.PENDING)
                .description(command.transaction().description())
                .build();

        return transactionRepositoryPortOut.save(financialTransaction)
                .flatMap(transactionStored ->
                        accountPort.validate(command.ledgerEntries().destinationAccountNumber())
                                .flatMap(idDestination -> {
                                    LedgerEntry originEntry = LedgerEntry.builder()
                                            .accountId(command.ledgerEntries().originAccountId())
                                            .transactionId(transactionStored.getId())
                                            .direction(Direction.DEBIT)
                                            .money(money)
                                            .build();
                                    LedgerEntry destinationEntry = LedgerEntry.builder()
                                            .accountId(idDestination)
                                            .transactionId(transactionStored.getId())
                                            .direction(Direction.CREDIT)
                                            .money(money)
                                            .build();

                                    return accountPort
                                            .debit(command.ledgerEntries().originAccountId(),money)
                                            .then(accountPort.credit(idDestination,money))
                                            .then(ledgerEntriesRepositoryPortOut.save(originEntry))
                                            .then(ledgerEntriesRepositoryPortOut.save(destinationEntry))
                                            .then(Mono.defer(() -> {
                                                transactionStored.setStatus(TransactionStatus.SUCCESS);
                                                transactionStored.setCreatedAt(LocalDateTime.now());
                                                return transactionRepositoryPortOut.save(transactionStored);
                                            }))
                                            .map(savedTransaction ->
                                                    new CreateTransferResult(
                                                            savedTransaction.getId(),
                                                            savedTransaction.getIdempotencyKey(),
                                                            savedTransaction.getType(),
                                                            savedTransaction.getMoney().amount(),
                                                            savedTransaction.getMoney().currency(),
                                                            savedTransaction.getStatus(),
                                                            originEntry.getAccountId(),
                                                            destinationEntry.getAccountId(),
                                                            savedTransaction.getCreatedAt()
                                                    )
                                            );
                                })
                );
    }
}
