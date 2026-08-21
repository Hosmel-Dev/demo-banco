package com.demo.bank.account.application.service;

import com.demo.bank.account.application.dto.command.TransactionAccountCommand;
import com.demo.bank.account.application.dto.result.TransactionAccountResult;
import com.demo.bank.account.application.port.in.CreditAccountUseCase;
import com.demo.bank.account.application.port.out.AccountRepositoryPortOut;
import com.demo.bank.account.domain.enums.AccountStatus;
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
                return null;
            }
            if(!account.getBalance().currency().equals(transactionAccountCommand.currency())){
                return null;
                //Posiblemente usamos los datos de Balance
            }
            account.getBalance().credit(transactionAccountCommand.amount());

            return repositoryPortOut.credit(account.getId(),transactionAccountCommand.amount()).map(accountCredited -> new TransactionAccountResult(
                    accountCredited.getId(),
                    accountCredited.getAccountNumber(),
                    accountCredited.getBalance(),
                    accountCredited.getCreatedAt()));
        });
    }
}
