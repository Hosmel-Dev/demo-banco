package com.demo.bank.transaction.infrastructure.adapter.in.rest.mapper;

import com.demo.bank.transaction.application.dto.command.CreateTransferCommand;
import com.demo.bank.transaction.application.dto.result.CreateTransferResult;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.request.CreateTransferRequest;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.response.CreateTransferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferRestMapper {
    @Mapping(source = "idempotencyKey", target = "idempotencyKey")
    @Mapping(source = "request.originAccountId", target = "ledgerEntries.originAccountId")
    @Mapping(source = "request.destinationAccountId", target = "ledgerEntries.destinationAccountId")
    @Mapping(source = "request.amount", target = "transaction.amount")
    @Mapping(source = "request.currency", target = "transaction.currency")
    @Mapping(source = "request.description", target = "transaction.description")
    CreateTransferCommand fromRequestToCommand(String idempotencyKey, CreateTransferRequest createTransferRequest);

    CreateTransferResponse fromResultToResponse(CreateTransferResult createTransferResult);
}
