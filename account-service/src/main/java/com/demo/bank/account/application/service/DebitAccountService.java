package com.demo.bank.account.application.service;

import com.demo.bank.account.application.dto.command.TransactionAccountCommand;
import com.demo.bank.account.application.dto.result.TransactionAccountResult;
import com.demo.bank.account.application.port.in.DebitAccountUseCase;
import com.demo.bank.account.application.port.out.AccountRepositoryPortOut;
import com.demo.bank.account.domain.enums.AccountStatus;
import com.demo.bank.account.domain.exception.AccountNotActiveException;
import com.demo.bank.account.domain.exception.AccountNotFoundException;
import com.demo.bank.account.domain.exception.DifferentCurrencyException;
import com.demo.bank.account.domain.exception.InsufficientFundsException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class DebitAccountService implements DebitAccountUseCase {

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

            if(!account.getBalance().validForDebit(transactionAccountCommand.amount())){
                return Mono.error(new InsufficientFundsException(
                        account.getAccountNumber(),
                        account.getBalance().balance(),
                        transactionAccountCommand.amount()
                ));
            }

            return repositoryPortOut.debit(account.getId(), transactionAccountCommand.amount())
                    .map(accountDebited -> new TransactionAccountResult(
                            accountDebited.getId(),
                            accountDebited.getAccountNumber(),
                            accountDebited.getBalance(),
                            accountDebited.getCreatedAt()));
        }).switchIfEmpty(Mono.error(new AccountNotFoundException(transactionAccountCommand.id())));
    }
}
