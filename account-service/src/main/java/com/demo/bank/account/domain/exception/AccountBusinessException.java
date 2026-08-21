package com.demo.bank.account.domain.exception;

import lombok.Getter;

@Getter
public class AccountBusinessException extends RuntimeException{
    private final String code;

    public AccountBusinessException(String code, String message){
        super(message);
        this.code = code;
    }

}
