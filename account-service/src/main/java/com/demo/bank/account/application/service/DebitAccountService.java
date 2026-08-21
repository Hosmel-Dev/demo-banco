package com.demo.bank.account.application.service;

import com.demo.bank.account.application.dto.command.TransactionAccountCommand;
import com.demo.bank.account.application.dto.result.TransactionAccountResult;
import com.demo.bank.account.application.port.in.DebitAccountUseCase;
import com.demo.bank.account.application.port.out.AccountRepositoryPortOut;
import com.demo.bank.account.domain.enums.AccountStatus;
import com.demo.bank.account.domain.enums.AppCurrency;
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
                return null;
            }
            if(!account.getBalance().currency().equals(transactionAccountCommand.currency())){
                return null;
            }

            account.getBalance().debit(transactionAccountCommand.amount());
            return repositoryPortOut.debit(account.getId(), transactionAccountCommand.amount())
                    .map(accountDebited -> new TransactionAccountResult(
                            accountDebited.getId(),
                            accountDebited.getAccountNumber(),
                            accountDebited.getBalance(),
                            accountDebited.getCreatedAt()));
        });
    }
}
