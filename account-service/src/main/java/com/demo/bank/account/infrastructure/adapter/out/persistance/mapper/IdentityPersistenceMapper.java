package com.demo.bank.account.infrastructure.adapter.out.persistance.mapper;

import com.demo.bank.account.domain.model.IdentityUser;
import com.demo.bank.account.infrastructure.adapter.out.persistance.entity.IdentityUserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IdentityPersistenceMapper {
    IdentityUser toDomain(IdentityUserEntity entity);
    IdentityUserEntity toEntity(IdentityUser domain);
}
