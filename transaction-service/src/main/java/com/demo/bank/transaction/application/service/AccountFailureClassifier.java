package com.demo.bank.transaction.application.service;

import com.demo.bank.transaction.infrastructure.exception.*;

public final class AccountFailureClassifier {
    private AccountFailureClassifier() {}

    public static boolean definitive(Throwable error) {
        return error instanceof AccountNotFoundException
                || error instanceof AccountNotActiveException
                || error instanceof DifferentCurrencyException
                || error instanceof InsufficientFundsException
                || error instanceof ForbiddenAccessException;
    }
}