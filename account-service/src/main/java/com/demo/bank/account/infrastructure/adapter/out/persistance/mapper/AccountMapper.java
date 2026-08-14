package com.demo.bank.account.infrastructure.adapter.out.persistance.mapper;

import com.demo.bank.account.domain.model.Account;
import com.demo.bank.account.infrastructure.adapter.in.rest.response.AccountResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountResponse toResponse(Account account);
}
