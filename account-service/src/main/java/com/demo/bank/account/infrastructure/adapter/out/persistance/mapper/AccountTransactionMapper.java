package com.demo.bank.account.infrastructure.adapter.out.persistance.mapper;

import com.demo.bank.account.application.dto.command.TransactionAccountCommand;
import com.demo.bank.account.application.dto.result.TransactionAccountResult;
import com.demo.bank.account.infrastructure.adapter.in.rest.request.TransactionAccountRequest;
import com.demo.bank.account.infrastructure.adapter.in.rest.response.TransactionAccountResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountTransactionMapper {
    TransactionAccountResponse toResponse(TransactionAccountResult transactionAccountResult);
    TransactionAccountCommand toCommand(TransactionAccountRequest transactionAccountRequest);
}
