package com.demo.bank.account.application.service;

import com.demo.bank.account.application.dto.command.TransactionAccountCommand;
import com.demo.bank.account.application.dto.result.TransactionAccountResult;
import com.demo.bank.account.application.port.in.CreditAccountUseCase;
import com.demo.bank.account.application.port.out.AccountRepositoryPortOut;
import com.demo.bank.account.domain.enums.AccountStatus;
import com.demo.bank.account.domain.exception.AccountNotActiveException;
import com.demo.bank.account.domain.exception.AccountNotFoundException;
import com.demo.bank.account.domain.exception.DifferentCurrencyException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class CreditAccountService implements CreditAccountUseCase {

    private final AccountRepositoryPortOut repositoryPortOut;

    @Override
    public Mono<TransactionAccountResult> execute(TransactionAccountCommand transactionAccountCommand) {
        return repositoryPortOut.findById(transactionAccountCommand.id()).flatMap(account -> {
            if(!account.getStatus().equals(AccountStatus.ACTIVE)){
                return Mono.error(new AccountNotActiveException(
                        account.getAccountNumber(),
                        account.getStatus()
                ));
            }
            if(!account.getBalance().currency().equals(transactionAccountCommand.currency())){
                return Mono.error(new DifferentCurrencyException(
                        account.getAccountNumber(),
                        transactionAccountCommand.currency(),
                        account.getBalance().currency()
                ));
            }
            account.getBalance().credit(transactionAccountCommand.amount());

            return repositoryPortOut.credit(account.getId(),transactionAccountCommand.amount()).map(accountCredited -> new TransactionAccountResult(
                    accountCredited.getId(),
                    accountCredited.getAccountNumber(),
                    accountCredited.getBalance(),
                    accountCredited.getCreatedAt()));
        }).switchIfEmpty(Mono.error(new AccountNotFoundException(transactionAccountCommand.id())));
    }
}
