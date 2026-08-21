package com.demo.bank.account.domain.exception;

import lombok.Getter;

@Getter
public class AccountNotFound extends AccountBusinessException {
    private final Long id;
    public AccountNotFound(Long id){
      super("ACCOUNT_NOT_FOUND", "La cuenta con id %s no existe"
              .formatted(id));
      this.id = id;
    }
}
