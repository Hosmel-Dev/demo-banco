package com.demo.bank.account.infrastructure.adapter.out.persistance.mapper;

import com.demo.bank.account.domain.model.Account;
import com.demo.bank.account.domain.model.AccountBalance;
import com.demo.bank.account.infrastructure.adapter.in.rest.request.AccountRequest;
import com.demo.bank.account.infrastructure.adapter.in.rest.response.AccountResponse;
import com.demo.bank.account.infrastructure.adapter.out.persistance.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(source = "balance.balance", target = "balance")
    @Mapping(source = "balance.currency", target = "currency")
    AccountResponse toResponse(Account account);

    @Mapping(source = ".", target = "balance")
    Account requestToDomain(AccountRequest account);

    @Mapping(source = "accountType", target = "type")
    @Mapping(source = ".", target = "balance")
    Account entityToDomain(AccountEntity account);

    @Mapping(source = "type", target = "accountType")
    @Mapping(source = "balance.balance", target = "balance")
    @Mapping(source = "balance.currency" , target = "currency")
    AccountEntity toEntity(Account account);

    default AccountBalance entityMapToAccountBalance(AccountEntity entity){
        if(entity == null || entity.getBalance() == null || entity.getCurrency() == null){
            return null;
        }
        return new AccountBalance(entity.getBalance(), entity.getCurrency());
    }

    default AccountBalance requestMapToAccountBalance(AccountRequest request){
        if(request == null || request.balance() == null || request.currency() == null){
            return null;
        }
        return new AccountBalance(request.balance(), request.currency());
    }

}
