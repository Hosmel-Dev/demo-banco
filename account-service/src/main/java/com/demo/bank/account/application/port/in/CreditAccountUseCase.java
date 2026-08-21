package com.demo.bank.account.application.port.in;

import com.demo.bank.account.application.dto.command.TransactionAccountCommand;
import com.demo.bank.account.application.dto.result.TransactionAccountResult;
import reactor.core.publisher.Mono;

public interface CreditAccountUseCase {
    Mono<TransactionAccountResult> execute(TransactionAccountCommand transactionAccountCommand);

}
