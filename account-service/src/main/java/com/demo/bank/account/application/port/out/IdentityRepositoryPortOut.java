package com.demo.bank.account.application.port.out;

import com.demo.bank.account.domain.model.IdentityUser;
import reactor.core.publisher.Mono;

public interface IdentityRepositoryPortOut {
    Mono<IdentityUser> findByUsername(String username);
}
