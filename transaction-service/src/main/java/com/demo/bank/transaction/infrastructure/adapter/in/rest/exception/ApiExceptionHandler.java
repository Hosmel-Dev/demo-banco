package com.demo.bank.transaction.infrastructure.adapter.in.rest.exception;

import com.demo.bank.transaction.domain.exception.FailedTransactionException;
import com.demo.bank.transaction.domain.exception.SameAccountTransferException;

import com.demo.bank.transaction.infrastructure.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ServerWebInputException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(SameAccountTransferException.class)
    public ProblemDetail handleSameAccountTransfer(SameAccountTransferException exception){
        ProblemDetail problem = newProblem(
                HttpStatus.valueOf(422),
                "/problems/same-account-transfer",
                "Cuenta no activa",
                exception.getMessage(),
                exception.getCode()
        );

        problem.setProperty(
                "accountId",
                exception.getAccountId()
        );

        return problem;
    }

    @ExceptionHandler(DifferentCurrencyException.class)
    public ProblemDetail handleDifferentCurrency(DifferentCurrencyException exception){
        ProblemDetail problem = newProblem(
                HttpStatus.valueOf(422),
                "/problems/same-account-transfer",
                "Cuentas de transferencia iguales",
                exception.getMessage(),
                exception.getCode()
        );

        if (exception.getCurrencyOriginAccount() != null ||
                exception.getCurrencyDestinationAccount() != null){

            problem.setProperty(
                    "currencyOriginAccount",
                    exception.getCurrencyOriginAccount()
            );

            problem.setProperty(
                    "currencyDestinationAccount",
                    exception.getCurrencyDestinationAccount()
            );
        }

        return problem;
    }

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
                exception.getAccountId()
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

    @ExceptionHandler(AccountNotActiveException.class)
    public ProblemDetail handleAccountNotActive(AccountNotActiveException exception){
        ProblemDetail problem = newProblem(
                HttpStatusCode.valueOf(423),
                "/problems/account-not-active",
                "Cuenta no activa",
                exception.getMessage(),
                exception.getCode()
        );

        problem.setProperty(
                "accountStatus",
                exception.getAccountStatus()
        );

        return problem;
    }

    @ExceptionHandler(FailedTransactionException.class)
    public ProblemDetail handleFailedTransaction(FailedTransactionException exception){
        ProblemDetail problem = newProblem(
                HttpStatusCode.valueOf(422),
                "/problems/unprocessable-content",
                "Fallo en la transacción",
                exception.getMessage(),
                exception.getCode()
        );

        problem.setProperty(
                "transactionId",
                exception.getTransactionId()
        );

        return problem;
    }

    @ExceptionHandler(AccountServiceException.class)
    public ProblemDetail handleAccountService(AccountServiceException exception){
        return newProblem(
                HttpStatusCode.valueOf(502),
                "/problems/communication-error",
                "Error en comunicación con servicio",
                exception.getMessage(),
                exception.getCode()
        );
    }

    @ExceptionHandler(AccountServiceUnavailableException.class)
    public ProblemDetail handleAccountServiceUnavailable(AccountServiceUnavailableException exception){
        return newProblem(
                HttpStatusCode.valueOf(503),
                "/problems/service-unavailable",
                "Servicio no disponible",
                exception.getMessage(),
                exception.getCode()
        );
    }

    @ExceptionHandler(MissingRequestValueException.class)
    public ProblemDetail handleMissingHeader(
            MissingRequestValueException exception){
        ProblemDetail problem = newProblem(
                HttpStatus.BAD_REQUEST,
                "/problems/validation-error",
                "Error de validación",
                "Uno o más parámetros no cumplen con las validaciones requeridas",
                "VALIDATION_ERROR"
        );


        problem.setProperty(
                "errors",
                Map.of(
                        exception.getName(),
                        "El valor es obligatorio"
                )
        );

        return problem;

    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail handleMethodValidation(
            HandlerMethodValidationException exception
    ){
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getParameterValidationResults().forEach(result ->{
            String field = result.getMethodParameter().getParameterName();
            String message = result.getResolvableErrors().isEmpty()
                    ? "Valor inválido"
                    : result.getResolvableErrors().getFirst().getDefaultMessage();
            errors.put(field,message);
        });

        ProblemDetail problem = newProblem(
                HttpStatusCode.valueOf(400),
                "/problems/validation-error",
                "Error de validación",
                "Uno o más campos no cumplen con las validaciones requeridas",
                "VALIDATION_ERROR"
        );

        problem.setProperty("errors",errors);

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
