package com.demo.bank.account.infrastructure.adapter.in.rest.mapper;

import com.demo.bank.account.application.dto.command.TransactionAccountCommand;
import com.demo.bank.account.application.dto.result.TransactionAccountResult;
import com.demo.bank.account.infrastructure.adapter.in.rest.dto.request.TransactionAccountRequest;
import com.demo.bank.account.infrastructure.adapter.in.rest.dto.response.TransactionAccountResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountTransactionRestMapper {
    TransactionAccountResponse toResponse(TransactionAccountResult transactionAccountResult);
    TransactionAccountCommand toCommand(TransactionAccountRequest transactionAccountRequest);
}
