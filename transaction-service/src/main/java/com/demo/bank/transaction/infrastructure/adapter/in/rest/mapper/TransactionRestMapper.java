package com.demo.bank.transaction.infrastructure.adapter.in.rest.mapper;

import com.demo.bank.transaction.application.dto.command.CreateTransactionCommand;
import com.demo.bank.transaction.application.dto.command.CreateTransferCommand;
import com.demo.bank.transaction.application.dto.result.CreateTransactionResult;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.request.CreateTransactionRequest;
import com.demo.bank.transaction.infrastructure.adapter.in.rest.response.CreateTransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionRestMapper {
    @Mapping(source = "idempotencyKey", target = "idempotencyKey")
    @Mapping(source = "request", target = "transaction")
    @Mapping(source = "request", target = "ledgerEntries")
    CreateTransactionCommand toCommand(String idempotencyKey, CreateTransactionRequest request);
    CreateTransactionCommand.FinancialTransaction toFinancialTransaction(CreateTransactionRequest request);
    CreateTransactionCommand.LedgerEntries toLedgerEntries(CreateTransactionRequest request);

    CreateTransactionResponse toResponse(CreateTransactionResult result);
}
