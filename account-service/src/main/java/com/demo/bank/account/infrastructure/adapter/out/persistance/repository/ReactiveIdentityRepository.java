package com.demo.bank.account.infrastructure.adapter.out.persistance.repository;

import com.demo.bank.account.infrastructure.adapter.out.persistance.entity.IdentityUserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ReactiveIdentityRepository extends ReactiveCrudRepository<IdentityUserEntity, Long> {
    Mono<IdentityUserEntity> findByUsername(String username);
}
