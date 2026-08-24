package com.demo.bank.transaction.application.port.out;

import com.demo.bank.transaction.application.dto.command.AccountTransferCommand;
import com.demo.bank.transaction.application.dto.result.AccountTransferResult;
import com.demo.bank.transaction.domain.model.Account;
import com.demo.bank.transaction.domain.model.Money;
import reactor.core.publisher.Mono;

public interface AccountPort {
    Mono<AccountTransferResult> debit(Long id, Money money);
    Mono<AccountTransferResult> credit(Long id, Money money);
    Mono<Long> validate(String accountNumber);
}
