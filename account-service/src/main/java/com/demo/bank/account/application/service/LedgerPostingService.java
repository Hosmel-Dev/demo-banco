package com.demo.bank.account.application.service;

import com.demo.bank.account.application.dto.command.TransactionAccountCommand;
import com.demo.bank.account.application.dto.result.TransactionAccountResult;
import com.demo.bank.account.application.port.out.LedgerEntryPortOut;
import com.demo.bank.account.domain.enums.Direction;
import com.demo.bank.account.domain.enums.EntryKind;
import com.demo.bank.account.domain.exception.LedgerConflictException;
import com.demo.bank.account.domain.model.Account;
import com.demo.bank.account.domain.model.LedgerEntry;
import com.demo.bank.account.domain.model.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LedgerPostingService {
    private final LedgerEntryPortOut entries;

    public Mono<TransactionAccountResult> replay(
            Account account,
            TransactionAccountCommand command,
            Direction direction) {
        return entries.find(
                        command.transactionId(),
                        command.id(),
                        direction,
                        command.entryKind())
                .map(existing -> {
                    verifyPayload(existing, command);
                    return result(
                            account,
                            new Money(
                                    existing.balanceAfter(),
                                    existing.currency()));
                });
    }

    public Mono<Optional<Long>> reversalId(
            TransactionAccountCommand command,
            Direction originalDirection) {
        if (command.entryKind() == EntryKind.ORIGINAL) {
            return Mono.just(Optional.empty());
        }

        return entries.find(
                        command.transactionId(),
                        command.id(),
                        originalDirection,
                        EntryKind.ORIGINAL)
                .switchIfEmpty(Mono.error(new LedgerConflictException(
                        "No existe el asiento original a compensar")))
                .map(original -> {
                    verifyPayload(original, command);
                    return Optional.of(original.id());
                });
    }

    public Mono<TransactionAccountResult> record(
            Account updated,
            TransactionAccountCommand command,
            Direction direction,
            Optional<Long> reversalId) {
        LedgerEntry entry = new LedgerEntry(
                null,
                command.transactionId(),
                command.id(),
                direction,
                command.amount(),
                command.currency(),
                updated.getBalance().amount(),
                command.entryKind(),
                reversalId.orElse(null),
                LocalDateTime.now());

        return entries.save(entry)
                .thenReturn(result(updated, updated.getBalance()));
    }

    private void verifyPayload(
            LedgerEntry existing,
            TransactionAccountCommand command) {
        if (existing.amount().compareTo(command.amount()) != 0
                || existing.currency() != command.currency()) {
            throw new LedgerConflictException(
                    "El asiento ya existe con otro importe o moneda");
        }
    }

    private TransactionAccountResult result(Account account, Money balance) {
        return new TransactionAccountResult(
                account.getId(),
                account.getAccountNumber(),
                balance,
                account.getCreatedAt());
    }
}