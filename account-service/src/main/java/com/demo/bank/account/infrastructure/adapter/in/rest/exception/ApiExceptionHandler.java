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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(InsufficientFundsException.class)
    public ProblemDetail handleInsufficientFunds(InsufficientFundsException exception){
        ProblemDetail problem = newProblem(
                HttpStatusCode.valueOf(400),
                "/problems/resource-not-enough-funds",
                "Saldo insuficiente",
                exception.getMessage(),
                exception.getCode()
        );

        problem.setProperty(
                "accountNumber",
                exception.getAccountNumber()
        );

        return problem;
    }

    @ExceptionHandler(DifferentCurrencyException.class)
    public ProblemDetail handleDifferentCurrency(DifferentCurrencyException exception){
        ProblemDetail problem = newProblem(
                HttpStatusCode.valueOf(422),
                "/problems/different-currency",
                "Divisa distinta",
                exception.getMessage(),
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

        return problem;
    }

    @ExceptionHandler(AccountNotActiveException.class)
    public ProblemDetail handelAccountNotActive(AccountNotActiveException exception){
        ProblemDetail problem = newProblem(
                HttpStatusCode.valueOf(423),
                "/problems/account-not-active",
                "Cuenta no activa",
                exception.getMessage(),
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
        ProblemDetail problem = newProblem(
                HttpStatus.NOT_FOUND,
                "/problems/account-not-found",
                "Cuenta no encontrada",
                exception.getMessage(),
                exception.getCode()
        );

        if(exception.getAccountId() == null){
            problem.setProperty(
                    "accountNumber",
                    exception.getAccountNumber()
            );
        }

        if(exception.getAccountNumber() == null){
            problem.setProperty(
                    "accountId",
                    exception.getAccountId()
            );
        }

        return problem;
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ProblemDetail handleValidationException(
            WebExchangeBindException exception
    ) {
        ProblemDetail problem = newProblem(
                HttpStatusCode.valueOf(400),
                "/problems/validation-error",
                "Error de validación",
                "Uno o más campos no cumplen con las validaciones requeridas",
                "VALIDATION_ERROR"
        );

        Map<String, String> errors = new LinkedHashMap<>();

        exception.getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        problem.setProperty("errors", errors);

        return problem;
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ProblemDetail handleServerWebInput(ServerWebInputException exception){
        ProblemDetail problem = newProblem(
                HttpStatusCode.valueOf(400),
                "/problems/invalid-json",
                "Petición no legible",
                "El formato del cuerpo de la petición es inválido o contiene valores no permitidos",
                "INVALID_REQUEST"
        );

        InvalidFormatException invalidCause =
                findCause(exception, InvalidFormatException.class);

        if (invalidCause != null && invalidCause.getTargetType().isEnum()){
            String fieldName = invalidCause.getPath().isEmpty()
                    ? "desconocido"
                    : invalidCause.getPath()
                    .getLast()
                    .getPropertyName();

            Object invalidValue = invalidCause.getValue();
            List<String> acceptedValues = Arrays.stream(
                            invalidCause.getTargetType().getEnumConstants()
                    ).map(value -> ((Enum<?>) value).name())
                    .toList();

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

    private ProblemDetail newProblem(
            HttpStatusCode status,
            String type,
            String title,
            String detail,
            String code
    ){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create(type));
        problem.setTitle(title);
        problem.setProperty("code", code);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
