package com.demo.bank.account.infrastructure.adapter.in.rest.exception;

import com.demo.bank.account.domain.exception.AccountNotActiveException;
import com.demo.bank.account.domain.exception.AccountNotFoundException;
import com.demo.bank.account.domain.exception.DifferentCurrencyException;
import com.demo.bank.account.domain.exception.InsufficientFundsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

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
                "code",
                exception.getCode()
        );

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
                "code",
                exception.getCode()
        );

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

    @ExceptionHandler(AccountNotFoundException.class)
    public ProblemDetail handleAccountNotFound(AccountNotFoundException exception){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );

        problem.setType(
                URI.create("problems/account-not-found")
        );

        problem.setTitle("Cuenta no encontrada");

        problem.setProperty(
                "code",
                exception.getCode()
        );

        if(exception.getId() == null){
            problem.setProperty(
                    "accountNumber",
                    exception.getAccountNumber()
            );
        }

        if(exception.getAccountNumber() == null){
            problem.setProperty(
                    "id",
                    exception.getId()
            );
        }

        problem.setProperty(
                "timestamp",
                Instant.now()
        );

        return problem;
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ProblemDetail handleValidationException(
            WebExchangeBindException exception
    ) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Uno o más campos no cumplen con las validaciones requeridas"
        );

        problem.setType(
                URI.create("/problems/validation-error")
        );

        problem.setTitle("Error de validación");

        Map<String, String> errors = new HashMap<>();

        exception.getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        problem.setProperty("errors", errors);
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ProblemDetail handleServerWebInput(ServerWebInputException exception){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "El formato del cuerpo de la petición es inválido o contiene valores no permitidos"
        );

        problem.setType(
                URI.create("problems/invalid-json")
        );

        problem.setTitle("Petición no legible");

        InvalidFormatException invalidCause =
                findCause(exception, InvalidFormatException.class);

        if (invalidCause != null && invalidCause.getTargetType().isEnum()){
            String fieldName = invalidCause.getPath().isEmpty()
                    ? "desconocido"
                    : invalidCause.getPath()
                    .getLast()
                    .getPropertyName();

            Object invalidValue = invalidCause.getValue();
            Class<?> acceptedValues = invalidCause.getTargetType();

            // Caso 1: enum

            problem.setDetail(
                    "El valor %s no es válido para el campo %s"
                            .formatted(invalidValue, fieldName)
            );

            problem.setProperty(
                    "invalidValue",
                    invalidValue
            );

            problem.setProperty(
                    "acceptedValues",
                    acceptedValues
            );
        }

        problem.setProperty(
                "timestamp",
                Instant.now()
        );

        return problem;
    }

    private <T extends Throwable> T findCause(
            Throwable exception,
            Class<T> type
    ) {
        Throwable current = exception;

        while (current != null){
            if (type.isInstance(current)){
                return type.cast(current);
            }

            current = current.getCause();
        }
        return null;
    }
}
