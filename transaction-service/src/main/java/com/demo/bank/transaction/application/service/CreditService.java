package com.demo.bank.transaction.application.service;

import com.demo.bank.transaction.application.dto.command.CreateTransactionCommand;
import com.demo.bank.transaction.application.dto.result.CreateTransactionResult;
import com.demo.bank.transaction.application.port.in.CreditUseCase;
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

@Service
@RequiredArgsConstructor
public class CreditService implements CreditUseCase {
    private final TransactionRepositoryPortOut transactionRepositoryPortOut;
    private final AccountPort accountPort;
    private final LedgerEntriesRepositoryPortOut ledgerEntriesRepositoryPortOut;


    @Override
    public Mono<CreateTransactionResult> execute(CreateTransactionCommand command) {
        var accountId = command.ledgerEntries().accountId();
        var money = createMoney(command);
        var transaction = createFinancialTransaction(command, money);

        return transactionRepositoryPortOut.save(transaction)
                .flatMap(saved ->
                    toCredit(saved,accountId,money)
                            .then(markAsSuccessful(saved))
                ).map(savedTransaction -> toResult(savedTransaction,accountId));

    }

    private Mono<Void> toCredit(FinancialTransaction transaction, Long accountId, Money money){
        var ledgerEntry = createLedgerEntry(transaction, accountId, money);

        return accountPort.credit(accountId,money)
                .then(ledgerEntriesRepositoryPortOut.save(ledgerEntry))
                .then();
    }

    private Mono<FinancialTransaction> markAsSuccessful(FinancialTransaction transaction){
        transaction.markAsSuccessful();
        return transactionRepositoryPortOut.save(transaction);
    }

    private Money createMoney(CreateTransactionCommand command){
        return new Money(command.transaction().amount(), command.transaction().currency());
    }

    private FinancialTransaction createFinancialTransaction(CreateTransactionCommand command, Money money){
        return FinancialTransaction.builder()
                .idempotencyKey(command.idempotencyKey())
                .type(TransactionType.DEPOSIT)
                .money(money)
                .status(TransactionStatus.PENDING)
                .description(command.transaction().description())
                .build();
    }

    private LedgerEntry createLedgerEntry(FinancialTransaction financialTransaction, Long accountId, Money money){
        return LedgerEntry.builder()
                .transactionId(financialTransaction.getId())
                .accountId(accountId)
                .direction(Direction.CREDIT)
                .money(money)
                .build();
    }

    private CreateTransactionResult toResult(FinancialTransaction transaction, Long accountId){
        return new CreateTransactionResult(
                transaction.getId(),
                transaction.getIdempotencyKey(),
                transaction.getType(),
                transaction.getMoney().amount(),
                transaction.getMoney().currency(),
                transaction.getStatus(),
                accountId,
                transaction.getCreatedAt()
        );
    }
}
