package com.demo.bank.transaction.infrastructure.adapter.in.rest;

import com.demo.bank.transaction.application.dto.command.CreateTransferCommand;
import com.demo.bank.transaction.application.port.in.TransferUseCase;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.mapper.TransferRestMapper;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.request.CreateTransferRequest;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.response.CreateTransferResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class TransactionController {
    private final TransferUseCase transferUseCase;
    private final TransferRestMapper restMapper;

    @PostMapping("/transfers")
    public Mono<CreateTransferResponse> createTransfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody CreateTransferRequest createTransferRequest){
        CreateTransferCommand command = restMapper.fromRequestToCommand(idempotencyKey, createTransferRequest);
        return transferUseCase.excecute(command).map(restMapper::fromResultToResponse);
    }
}
