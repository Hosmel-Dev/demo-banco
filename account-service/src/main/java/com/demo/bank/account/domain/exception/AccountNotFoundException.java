package com.demo.bank.account.domain.exception;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends AccountBusinessException {
    private final Long accountId;
    private final String accountNumber;
    public AccountNotFoundException(Long accountId){
      super("ACCOUNT_NOT_FOUND", "La cuenta con id %s no existe"
              .formatted(accountId));
      this.accountId = accountId;
      this.accountNumber = null;
    }

    public AccountNotFoundException(String accountNumber){
        super("ACCOUNT_NOT_FOUND", "La cuenta con número %s no existe"
                .formatted(accountNumber));
        this.accountNumber = accountNumber;
        this.accountId = null;
    }
}
