package com.demo.bank.account.application.port.out;

import reactor.core.publisher.Mono;

public interface PasswordVerifierPort {
    Mono<Boolean> matches(String passwordProvides, String actualPassword);
}
