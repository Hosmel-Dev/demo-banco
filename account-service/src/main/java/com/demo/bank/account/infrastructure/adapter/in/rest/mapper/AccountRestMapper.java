package com.demo.bank.account.infrastructure.adapter.in.rest.mapper;

import com.demo.bank.account.domain.model.Account;
import com.demo.bank.account.domain.model.Money;
import com.demo.bank.account.infrastructure.adapter.in.rest.dto.request.AccountRequest;
import com.demo.bank.account.infrastructure.adapter.in.rest.dto.response.AccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountRestMapper {
    AccountResponse toResponse(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = ".", target = "balance")
    Account requestToDomain(AccountRequest account);


    default Money requestMapToAccountBalance(AccountRequest request){
        if(request == null || request.balance() == null || request.currency() == null){
            return null;
        }
        return new Money(request.balance(), request.currency());
    }
}
