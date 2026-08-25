package com.demo.bank.transaction.infrastructure.adapter.in.rest;

import com.demo.bank.transaction.application.dto.command.CreateTransactionCommand;
import com.demo.bank.transaction.application.dto.command.CreateTransferCommand;
import com.demo.bank.transaction.application.port.in.CreditUseCase;
import com.demo.bank.transaction.application.port.in.DebitUseCase;
import com.demo.bank.transaction.application.port.in.TransferUseCase;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.mapper.TransactionRestMapper;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.mapper.TransferRestMapper;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.request.CreateTransactionRequest;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.request.CreateTransferRequest;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.response.CreateTransactionResponse;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.response.CreateTransferResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class TransactionController {
    private final TransferUseCase transferUseCase;
    private final DebitUseCase debitUseCase;
    private final CreditUseCase creditUseCase;
    private final TransferRestMapper transferRestMapper;
    private final TransactionRestMapper transactionRestMapper;


    @PostMapping("transactions/transfers")
    public Mono<CreateTransferResponse> registerTransference(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody CreateTransferRequest createTransferRequest){
        CreateTransferCommand command = transferRestMapper.fromRequestToCommand(idempotencyKey, createTransferRequest);
        return transferUseCase.execute(command).map(transferRestMapper::fromResultToResponse);
    }

    @PostMapping("/transactions/debit")
    public Mono<CreateTransactionResponse> registerDebit(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody CreateTransactionRequest createTransactionRequest){
        CreateTransactionCommand command = transactionRestMapper.toCommand(idempotencyKey, createTransactionRequest);
        return debitUseCase.execute(command).map(transactionRestMapper::toResponse);
    }

    @PostMapping("/transactions/credit")
    public Mono<CreateTransactionResponse> registerCredit(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody CreateTransactionRequest createTransactionRequest){
        CreateTransactionCommand command = transactionRestMapper.toCommand(idempotencyKey, createTransactionRequest);
        return creditUseCase.execute(command).map(transactionRestMapper::toResponse);
    }
}
