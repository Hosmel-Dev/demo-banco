package com.demo.bank.account.infrastructure.adapter.in.rest;

import com.demo.bank.account.application.port.in.*;
import com.demo.bank.account.infrastructure.adapter.in.rest.dto.request.AccountRequest;
import com.demo.bank.account.infrastructure.adapter.in.rest.dto.request.TransactionAccountRequest;
import com.demo.bank.account.infrastructure.adapter.in.rest.dto.response.AccountResponse;
import com.demo.bank.account.infrastructure.adapter.in.rest.dto.response.TransactionAccountResponse;
import com.demo.bank.account.infrastructure.adapter.in.rest.mapper.AccountRestMapper;
import com.demo.bank.account.infrastructure.adapter.in.rest.mapper.AccountTransactionRestMapper;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/accounts")
public class AccountController {
    private final GetAccountUseCase getAccountsUseCase;
    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountByNumberUseCase getAccountByNumberUseCase;
    private final DebitAccountUseCase debitAccountUseCase;
    private final CreditAccountUseCase creditAccountUseCase;
    private final AccountRestMapper mapper;
    private final AccountTransactionRestMapper transactionMapper;

    @GetMapping("/{id}")
    public Mono<AccountResponse> getAccount(@NonNull @PathVariable Long id){
        return getAccountsUseCase.getAccount(id).map(mapper::toResponse);
    }

    @GetMapping("/number/{account-number}")
    public Mono<AccountResponse> getByAccountNumber(
            @NonNull @PathVariable("account-number") String accountNumber){
        return getAccountByNumberUseCase.execute(accountNumber).map(mapper::toResponse);
    }

    @PostMapping
    public Mono<AccountResponse> createAccount(@Valid @RequestBody AccountRequest accountRequest){
        return createAccountUseCase.createAccount
                (mapper.requestToDomain(accountRequest)).map(mapper::toResponse);
    }

    @PostMapping("/credit")
    public Mono<TransactionAccountResponse> creditAccount(
            @NonNull @Valid @RequestBody TransactionAccountRequest transactionAccountRequest){
        var command = transactionMapper.toCommand(transactionAccountRequest);
        return creditAccountUseCase.execute(command).map(transactionMapper::toResponse);
    }

    @PostMapping("/debit")
    public Mono<TransactionAccountResponse> debitAccount(
            @NonNull @Valid @RequestBody TransactionAccountRequest transactionAccountRequest){
        var command = transactionMapper.toCommand(transactionAccountRequest);
        return debitAccountUseCase.execute(command).map(transactionMapper::toResponse);
    }




}
