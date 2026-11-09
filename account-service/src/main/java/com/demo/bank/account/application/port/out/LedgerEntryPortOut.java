package com.demo.bank.account.application.port.out;

import com.demo.bank.account.domain.enums.Direction;
import com.demo.bank.account.domain.enums.EntryKind;
import com.demo.bank.account.domain.model.LedgerEntry;
import reactor.core.publisher.Mono;

public interface LedgerEntryPortOut {
    Mono<LedgerEntry> find(
            Long transactionId, Long accountId,
            Direction direction, EntryKind entryKind);

    Mono<LedgerEntry> save(LedgerEntry entry);
}