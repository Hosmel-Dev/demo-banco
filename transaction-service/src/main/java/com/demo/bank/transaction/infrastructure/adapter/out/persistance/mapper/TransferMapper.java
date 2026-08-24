package com.demo.bank.transaction.infrastructure.adapter.out.persistance.mapper;

import com.demo.bank.transaction.domain.model.FinancialTransaction;
import com.demo.bank.transaction.domain.model.Money;
import com.demo.bank.transaction.infrastructure.adapter.out.persistance.entity.FinancialTransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferMapper {
    @Mapping(source = ".", target = "money")
    FinancialTransaction toDomain(FinancialTransactionEntity financialTransactionEntity);

    @Mapping(source = "money.amount", target = "amount")
    @Mapping(source = "money.currency", target = "currency")
    FinancialTransactionEntity toEntity(FinancialTransaction financialTransaction);

    default Money entityMapToFinancialTransaction(FinancialTransactionEntity entity){
        if (entity == null || entity.getAmount() == null || entity.getCurrency() == null){
            return null;
        }
        return new Money(entity.getAmount(),entity.getCurrency());
    }
}
