package com.demo.bank.account.infrastructure.adapter.out.persistance;

import com.demo.bank.account.application.port.out.IdentityRepositoryPortOut;
import com.demo.bank.account.domain.model.IdentityUser;
import com.demo.bank.account.infrastructure.adapter.out.persistance.mapper.IdentityPersistenceMapper;
import com.demo.bank.account.infrastructure.adapter.out.persistance.repository.ReactiveIdentityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class IdentityPersistenceAdapter implements IdentityRepositoryPortOut {
    private final ReactiveIdentityRepository identityRepository;
    private final IdentityPersistenceMapper mapper;

    @Override
    public Mono<IdentityUser> findByUsername(String username) {
        return identityRepository.findByUsername(username).map(mapper::toDomain);
    }
}
