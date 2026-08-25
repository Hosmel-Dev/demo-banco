package com.demo.bank.transaction.application.service;


import com.demo.bank.transaction.application.dto.command.CreateTransactionCommand;
import com.demo.bank.transaction.application.dto.result.CreateTransactionResult;
import com.demo.bank.transaction.application.port.in.DebitUseCase;
import com.demo.bank.transaction.application.port.out.TransactionRepositoryPortOut;
import com.demo.bank.transaction.domain.enums.TransactionStatus;
import com.demo.bank.transaction.domain.enums.TransactionType;
import com.demo.bank.transaction.domain.model.FinancialTransaction;
import com.demo.bank.transaction.domain.model.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DebitService implements DebitUseCase {
    private final TransactionRepositoryPortOut transactionRepositoryPortOut;

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

//        return transactionRepositoryPortOut.save(financialTransaction).flatMap(savedTransaction -> {
//
//        });

        return null;
    }
}
