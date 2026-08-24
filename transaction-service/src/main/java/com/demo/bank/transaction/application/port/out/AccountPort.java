package com.demo.bank.transaction.application.port.out;

import com.demo.bank.transaction.application.dto.command.AccountTransferCommand;
import com.demo.bank.transaction.application.dto.result.AccountTransferResult;
import com.demo.bank.transaction.domain.model.Account;
import reactor.core.publisher.Mono;

public interface AccountPort {
    Mono<AccountTransferResult> debit(AccountTransferCommand account);
    Mono<AccountTransferResult> credit(AccountTransferCommand account);
}
