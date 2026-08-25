package com.demo.bank.transaction.application.service;

import com.demo.bank.transaction.application.dto.command.CreateTransactionCommand;
import com.demo.bank.transaction.application.dto.result.CreateTransactionResult;
import com.demo.bank.transaction.application.port.in.CreditUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreditService implements CreditUseCase {
    @Override
    public Mono<CreateTransactionResult> execute(CreateTransactionCommand command) {
        return null;
    }
}
