package com.demo.bank.account.infrastructure.adapter.out.persistance.mapper;

import com.demo.bank.account.domain.model.LedgerEntry;
import com.demo.bank.account.infrastructure.adapter.out.persistance.entity.LedgerEntryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LedgerEntryMapper {
    LedgerEntry toDomain(LedgerEntryEntity entity);

    @Mapping(target = "description", ignore = true)
    LedgerEntryEntity toEntity(LedgerEntry domain);
}
