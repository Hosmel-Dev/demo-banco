package com.demo.bank.transaction.application.port.in;

import com.demo.bank.transaction.application.dto.command.CreateTransferCommand;
import com.demo.bank.transaction.application.dto.result.CreateTransferResult;
import com.demo.bank.transaction.domain.model.FinancialTransaction;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.response.CreateTransferResponse;
import reactor.core.publisher.Mono;

public interface TransferUseCase {
    Mono<CreateTransferResult> excecute(CreateTransferCommand createTransactionCommand);
}
