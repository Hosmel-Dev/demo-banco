package com.demo.bank.account.infrastructure.adapter.in.rest.exception;

import com.demo.bank.account.domain.exception.AccountNotActiveException;
import com.demo.bank.account.domain.exception.DifferentCurrencyException;
import com.demo.bank.account.domain.exception.InsufficientFundsException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(InsufficientFundsException.class)
    public ProblemDetail handleResourceNotFound(InsufficientFundsException exception){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(400),
                exception.getMessage()
        );

        problem.setType(
                URI.create("/problems/resource-not-enough-funds")
        );

        problem.setTitle("Saldo insuficiente");

        problem.setProperty(
                "code",
                exception.getCode()
        );

        problem.setProperty(
                "Account number",
                exception.getAccountNumber()
        );

        problem.setProperty(
                "timestamp",
                Instant.now()
        );

        return problem;
    }

    @ExceptionHandler(DifferentCurrencyException.class)
    public ProblemDetail handleDifferentCurrency(DifferentCurrencyException exception){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(422),
                exception.getMessage()
        );

        problem.setType(
                URI.create("/problems/different-currency")
        );

        problem.setTitle("Divisa distinta");

        problem.setProperty(
                "currencyAccount",
                exception.getCurrencyAccount()
        );

        problem.setProperty(
                "currencyEntrance",
                exception.getCurrencyEntrance()
        );

        problem.setProperty(
                "accountNumber",
                exception.getAccountNumber()
        );

        problem.setProperty(
                "timestamp",
                Instant.now()
        );

        return problem;
    }

    @ExceptionHandler(AccountNotActiveException.class)
    public ProblemDetail handelAccountNotActive(AccountNotActiveException exception){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(423),
                exception.getMessage()
        );

        problem.setType(
                URI.create("problems/account-not-active")
        );

        problem.setTitle("Cuenta no activa");

        problem.setProperty(
                "accountNumber",
                exception.getAccountNumber()
        );

        problem.setProperty(
                "accountStatus",
                exception.getStatus()
        );

        return problem;
    }
}
