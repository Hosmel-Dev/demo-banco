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


@Service
@RequiredArgsConstructor
public class DebitService implements DebitUseCase {
    private final TransactionRepositoryPortOut transactionRepositoryPortOut;
    private final AccountPort accountPort;
    private final LedgerEntriesRepositoryPortOut ledgerEntriesRepositoryPortOut;

    @Override
    public Mono<CreateTransactionResult> execute(CreateTransactionCommand command) {
        var money = createMoney(command);
        var accountId = command.ledgerEntries().accountId();
        var transaction = createFinancialTransaction(command, money);

        return transactionRepositoryPortOut.save(transaction)
                .flatMap(saved ->
                    toDebit(saved, money, accountId)
                            .then(markAsSuccessful(saved,accountId)))
                .map(successful ->
                        toResult(successful, accountId));

    }

    private Mono<Void> toDebit(FinancialTransaction financialTransaction, Money money, Long accountId){
        var ledgerEntry = createLedgerEntry(financialTransaction,money,accountId);
        return accountPort.debit(accountId,money)
                .then(ledgerEntriesRepositoryPortOut.save(ledgerEntry))
                .then();
    }

    private Mono<FinancialTransaction> markAsSuccessful(FinancialTransaction financialTransaction, Long accountId){
        financialTransaction.markAsSuccessful();
        return transactionRepositoryPortOut.save(financialTransaction);
    }

    private Money createMoney(CreateTransactionCommand command){
        return new Money(
                command.transaction().amount(),
                command.transaction().currency()
        );
    }

    private FinancialTransaction createFinancialTransaction(CreateTransactionCommand command, Money money){
        return FinancialTransaction.builder()
                .idempotencyKey(command.idempotencyKey())
                .type(TransactionType.WITHDRAWAL)
                .money(money)
                .status(TransactionStatus.PENDING)
                .description(command.transaction().description())
                .build();
    }

    private LedgerEntry createLedgerEntry(FinancialTransaction transaction, Money money, Long accountId){
        return LedgerEntry.builder()
                .transactionId(transaction.getId())
                .accountId(accountId)
                .direction(Direction.DEBIT)
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
