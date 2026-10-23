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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@RequestMapping("/v1/transactions")
@RestController
public class TransactionController {
    private final TransferUseCase transferUseCase;
    private final DebitUseCase debitUseCase;
    private final CreditUseCase creditUseCase;
    private final TransferRestMapper transferRestMapper;
    private final TransactionRestMapper transactionRestMapper;


    @PostMapping("/transfers")
    public Mono<CreateTransferResponse> registerTransference(
            @RequestHeader("Idempotency-Key")
            @NotBlank(message = "Idempotency-Key es obligatorio")
            @Size(min = 36, max = 36,message = "Idempotency-Key debe tener 36 caracteres")
            String idempotencyKey,
            @Valid @RequestBody CreateTransferRequest createTransferRequest){
        CreateTransferCommand command = transferRestMapper.fromRequestToCommand(idempotencyKey, createTransferRequest);
        return transferUseCase.execute(command).map(transferRestMapper::fromResultToResponse);
    }

    @PostMapping("/debit")
    public Mono<CreateTransactionResponse> registerDebit(
            @RequestHeader("Idempotency-Key")
            @NotBlank(message = "Idempotency-Key es obligatorio")
            @Size(min = 36, max = 36,message = "Idempotency-Key debe tener 36 caracteres")
            String idempotencyKey,
            @Valid @RequestBody CreateTransactionRequest createTransactionRequest,
            @AuthenticationPrincipal Jwt jwt){
        CreateTransactionCommand command = transactionRestMapper.toCommand(idempotencyKey, createTransactionRequest);
        return debitUseCase.execute(command, Long.valueOf(Objects.requireNonNull(jwt.getSubject()))).map(transactionRestMapper::toResponse);
    }

    @PostMapping("/credit")
    public Mono<CreateTransactionResponse> registerCredit(
            @RequestHeader("Idempotency-Key")
            @NotBlank(message = "Idempotency-Key es obligatorio")
            @Size(min = 36, max = 36,message = "Idempotency-Key debe tener 36 caracteres")
            String idempotencyKey,
            @Valid @RequestBody CreateTransactionRequest createTransactionRequest){
        CreateTransactionCommand command = transactionRestMapper.toCommand(idempotencyKey, createTransactionRequest);
        return creditUseCase.execute(command).map(transactionRestMapper::toResponse);
    }

    @GetMapping("/me")
    public Mono<Map<String, String>> me(@AuthenticationPrincipal Jwt jwt) {
        return Mono.just(Map.of("userId", Objects.requireNonNull(jwt.getSubject())));
    }
}
