package com.demo.bank.transaction.infrastructure.audit;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

@Aspect
@Component
public class ApiAuditAspect {
    private static final Logger log = LoggerFactory.getLogger(ApiAuditAspect.class);

    @Around("within(com.demo.bank.transaction.infrastructure.adapter.in.rest..*) "
            + "&& @within(org.springframework.web.bind.annotation.RestController)")
    public Object audit(ProceedingJoinPoint call) throws Throwable {
        String auditId = UUID.randomUUID().toString();
        String operation = call.getSignature().getDeclaringType().getSimpleName()
                + "." + call.getSignature().getName();
        long started = System.nanoTime();

        log.info("event=api_audit phase=entry audit_id={} operation={}",
                auditId, operation);

        try {
            Object result = call.proceed();

            if (result instanceof Mono<?> mono) {
                return mono.doFinally(signal ->
                        logExit(auditId, operation, started, outcome(signal)));
            }
            if (result instanceof Flux<?> flux) {
                return flux.doFinally(signal ->
                        logExit(auditId, operation, started, outcome(signal)));
            }

            logExit(auditId, operation, started, "success");
            return result;
        } catch (Throwable error) {
            logExit(auditId, operation, started, "error");
            throw error;
        }
    }

    private static String outcome(SignalType signal) {
        if (signal == SignalType.ON_COMPLETE) return "success";
        if (signal == SignalType.ON_ERROR) return "error";
        if (signal == SignalType.CANCEL) return "cancel";
        return "other";
    }

    private static void logExit(String auditId, String operation,
                                long started, String outcome) {
        long durationMs = TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - started);
        log.info("event=api_audit phase=exit audit_id={} operation={} outcome={} duration_ms={}",
                auditId, operation, outcome, durationMs);
    }
}