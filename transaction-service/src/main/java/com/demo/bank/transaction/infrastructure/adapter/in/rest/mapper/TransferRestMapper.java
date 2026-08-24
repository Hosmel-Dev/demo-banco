package com.demo.bank.transaction.infrastructure.adapter.in.rest.mapper;

import com.demo.bank.transaction.application.dto.command.CreateTransferCommand;
import com.demo.bank.transaction.application.dto.result.CreateTransferResult;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.request.CreateTransferRequest;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.response.CreateTransferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferRestMapper {
//    @Mapping(source = "idempotencyKey", target = "idempotencyKey")
//    @Mapping(source = "createTransferRequest.originAccountId", target = "ledgerEntries.originAccountId")
//    @Mapping(source = "createTransferRequest.destinationAccountId", target = "ledgerEntries.destinationAccountId")
//    @Mapping(source = "createTransferRequest.amount", target = "transaction.amount")
//    @Mapping(source = "createTransferRequest.currency", target = "transaction.currency")
//    @Mapping(source = "createTransferRequest.description", target = "transaction.description")
//    CreateTransferCommand fromRequestToCommand(String idempotencyKey, CreateTransferRequest createTransferRequest);

    @Mapping(source = "idempotencyKey", target = "idempotencyKey")
    @Mapping(source = "request", target = "transaction")
    @Mapping(source = "request", target = "ledgerEntries")
    CreateTransferCommand fromRequestToCommand(String idempotencyKey, CreateTransferRequest request);

    CreateTransferCommand.FinancialTransaction toFinancialTransaction(CreateTransferRequest request);
    CreateTransferCommand.LedgerEntries toLedgerEntries(CreateTransferRequest request);


    CreateTransferResponse fromResultToResponse(CreateTransferResult createTransferResult);
}
