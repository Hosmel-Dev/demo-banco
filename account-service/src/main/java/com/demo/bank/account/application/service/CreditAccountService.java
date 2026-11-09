package com.demo.bank.account.application.service;

import com.demo.bank.account.application.dto.command.TransactionAccountCommand;
import com.demo.bank.account.application.dto.result.TransactionAccountResult;
import com.demo.bank.account.application.port.in.CreditAccountUseCase;
import com.demo.bank.account.application.port.out.AccountRepositoryPortOut;
import com.demo.bank.account.domain.enums.AccountStatus;
import com.demo.bank.account.domain.enums.Direction;
import com.demo.bank.account.domain.enums.EntryKind;
import com.demo.bank.account.domain.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreditAccountService implements CreditAccountUseCase {
    private final AccountRepositoryPortOut accounts;
    private final LedgerPostingService posting;
    private final TransactionalOperator transactions;

    @Override
    public Mono<TransactionAccountResult> execute(
            TransactionAccountCommand command) {
        Mono<TransactionAccountResult> operation = accounts
                .findByIdForUpdate(command.id())
                .switchIfEmpty(Mono.error(
                        new AccountNotFoundException(command.id())))
                .flatMap(account ->
                        posting.replay(account, command, Direction.CREDIT)
                                .switchIfEmpty(Mono.defer(() ->
                                        posting.reversalId(
                                                        command,
                                                        Direction.DEBIT)
                                                .flatMap(reversalId -> {
                                                    if (command.entryKind()
                                                            == EntryKind.ORIGINAL
                                                            && account.getStatus()
                                                            != AccountStatus.ACTIVE) {
                                                        return Mono.error(
                                                                new AccountNotActiveException(
                                                                        account.getAccountNumber(),
                                                                        account.getStatus()));
                                                    }
                                                    if (account.getBalance().currency()
                                                            != command.currency()) {
                                                        return Mono.error(
                                                                new DifferentCurrencyException(
                                                                        account.getAccountNumber(),
                                                                        command.currency(),
                                                                        account.getBalance().currency()));
                                                    }

                                                    return accounts
                                                            .credit(
                                                                    command.id(),
                                                                    command.amount())
                                                            .flatMap(updated ->
                                                                    posting.record(
                                                                            updated,
                                                                            command,
                                                                            Direction.CREDIT,
                                                                            reversalId));
                                                }))));

        return transactions.transactional(operation);
    }
}