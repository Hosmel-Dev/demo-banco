package com.demo.bank.account.domain.exception;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends AccountBusinessException {
    private final Long id;
    private final String accountNumber;
    public AccountNotFoundException(Long id){
      super("ACCOUNT_NOT_FOUND", "La cuenta con id %s no existe"
              .formatted(id));
      this.id = id;
      this.accountNumber = null;
    }

    public AccountNotFoundException(String accountNumber){
        super("ACCOUNT_NOT_FOUND", "La cuenta con número %s no existe"
                .formatted(accountNumber));
        this.accountNumber = accountNumber;
        this.id = null;
    }
}
