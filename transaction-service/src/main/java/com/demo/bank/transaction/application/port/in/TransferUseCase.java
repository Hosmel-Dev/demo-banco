package com.demo.bank.transaction.application.port.in;

import com.demo.bank.transaction.application.dto.command.CreateTransferCommand;
import com.demo.bank.transaction.application.dto.result.CreateTransferResult;
import reactor.core.publisher.Mono;

public interface TransferUseCase {
    Mono<CreateTransferResult> execute(CreateTransferCommand createTransactionCommand);
}
