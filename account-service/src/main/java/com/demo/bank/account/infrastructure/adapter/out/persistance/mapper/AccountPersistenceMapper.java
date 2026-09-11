package com.demo.bank.account.infrastructure.adapter.out.persistance.mapper;

import com.demo.bank.account.domain.model.Account;
import com.demo.bank.account.domain.model.Money;
import com.demo.bank.account.infrastructure.adapter.out.persistance.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountPersistenceMapper {
    @Mapping(source = ".", target = "balance")
    Account entityToDomain(AccountEntity account);


    @Mapping(source = "balance.amount", target = "balance")
    @Mapping(source = "balance.currency" , target = "currency")
    AccountEntity toEntity(Account account);

    default Money entityMapToAccountBalance(AccountEntity entity){
        if(entity == null || entity.getBalance() == null || entity.getCurrency() == null){
            return null;
        }
        return new Money(entity.getBalance(), entity.getCurrency());
    }

}
