package com.demo.bank.transaction.application.port.in;

import com.demo.bank.transaction.application.dto.command.CreateTransactionCommand;
import com.demo.bank.transaction.application.dto.result.CreateTransactionResult;
import reactor.core.publisher.Mono;

public interface CreditUseCase {
    Mono<CreateTransactionResult> execute(CreateTransactionCommand command);
}
