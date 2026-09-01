package com.demo.bank.transaction.infrastructure.adapter.in.rest.exception;

import com.demo.bank.transaction.domain.exception.AccountNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(AccountNotFoundException.class)
    private ProblemDetail handleDestinyAccountNotFound(AccountNotFoundException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );

        problemDetail.setType(
                URI.create("/problems/destiny-account-not-found")
        );

        problemDetail.setTitle("Cuenta no encontrada");

        problemDetail.setProperty(
                "code",
                exception.getCode()
        );

        if (exception.getAccountNumber() != null){
            problemDetail.setProperty(
                    "accountNumber",
                    exception.getAccountNumber()
            );
        }

        if (exception.getAccountId() != null){
            problemDetail.setProperty(
                    "accountId",
                    exception.getAccountId()
            );
        }


        return problemDetail;
    }
}
