package com.demo.bank.account.application.service;

import com.demo.bank.account.application.dto.command.TransactionAccountCommand;
import com.demo.bank.account.application.dto.result.TransactionAccountResult;
import com.demo.bank.account.application.port.in.DebitAccountUseCase;
import com.demo.bank.account.application.port.out.AccountRepositoryPortOut;
import com.demo.bank.account.domain.enums.AccountStatus;
import com.demo.bank.account.domain.enums.Direction;
import com.demo.bank.account.domain.enums.EntryKind;
import com.demo.bank.account.domain.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DebitAccountService implements DebitAccountUseCase {
    private final AccountRepositoryPortOut accounts;
    private final LedgerPostingService posting;
    private final TransactionalOperator transactions;

    @Override
    public Mono<TransactionAccountResult> execute(
            TransactionAccountCommand command,
            Long requesterUserId) {
        Mono<TransactionAccountResult> operation = accounts
                .findByIdForUpdate(command.id())
                .switchIfEmpty(Mono.error(
                        new AccountNotFoundException(command.id())))
                .flatMap(account -> {
                    if (command.entryKind() == EntryKind.ORIGINAL
                            && !Objects.equals(
                            account.getOwnerUserId(),
                            requesterUserId)) {
                        return Mono.error(new ForbiddenAccessException());
                    }

                    return posting.replay(account, command, Direction.DEBIT)
                            .switchIfEmpty(Mono.defer(() ->
                                    posting.reversalId(
                                                    command,
                                                    Direction.CREDIT)
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
                                                if (!account.getBalance()
                                                        .validForDebit(
                                                                command.amount())) {
                                                    return Mono.error(
                                                            new InsufficientFundsException(
                                                                    account.getAccountNumber(),
                                                                    account.getBalance().amount(),
                                                                    command.amount()));
                                                }

                                                return accounts
                                                        .debit(
                                                                command.id(),
                                                                command.amount())
                                                        .flatMap(updated ->
                                                                posting.record(
                                                                        updated,
                                                                        command,
                                                                        Direction.DEBIT,
                                                                        reversalId));
                                            })));
                });

        return transactions.transactional(operation);
    }
}