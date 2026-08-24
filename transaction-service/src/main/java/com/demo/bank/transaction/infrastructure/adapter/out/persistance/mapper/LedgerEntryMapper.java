package com.demo.bank.transaction.infrastructure.adapter.out.persistance.mapper;

import com.demo.bank.transaction.domain.model.LedgerEntry;
import com.demo.bank.transaction.domain.model.Money;
import com.demo.bank.transaction.infrastructure.adapter.out.persistance.entity.LedgerEntryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LedgerEntryMapper {
    @Mapping(source = "money.amount", target = "amount")
    @Mapping(source = "money.currency", target = "currency")
    LedgerEntry toDomain(LedgerEntryEntity ledgerEntryEntity);

    @Mapping(source = ".", target = "money")
    LedgerEntryEntity toEntity(LedgerEntry ledgerEntry);

    default Money fromEntityToDomain(LedgerEntryEntity entity){
        if(entity == null || entity.getAmount() == null || entity.getCurrency() == null){
            return null;
        }
        return new Money(entity.getAmount(), entity.getCurrency());
    }
}
