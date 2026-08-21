package com.demo.bank.account.domain.exception;

import com.demo.bank.account.domain.enums.AccountStatus;
import lombok.Getter;

@Getter
public class AccountNotActiveException extends AccountBusinessException{
    private final String accountNumber;
    private final AccountStatus status;

    public AccountNotActiveException(String accountNumber, AccountStatus status){
        super("ACCOUNT_NOT_ACTIVE","El estado actual de la cuenta %s es %s"
                .formatted(accountNumber, status));
        this.accountNumber = accountNumber;
        this.status = status;
    }
}
