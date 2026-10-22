package com.demo.bank.account.infrastructure.adapter.out.persistance;

import com.demo.bank.account.application.port.out.PasswordVerifierPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class BCryptPasswordVerifierAdapter implements PasswordVerifierPort {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    @Override
    public Mono<Boolean> matches(String rawPassword, String passwordHash) {
//        return Mono.just(encoder.matches(rawPassword, passwordHash));
        return Mono.fromCallable(() -> encoder.matches(rawPassword, passwordHash))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
