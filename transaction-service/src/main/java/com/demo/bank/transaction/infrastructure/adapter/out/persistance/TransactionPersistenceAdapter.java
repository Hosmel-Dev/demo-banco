package com.demo.bank.transaction.infrastructure.adapter.out.persistance;

import com.demo.bank.transaction.application.port.out.TransactionRepositoryPortOut;
import com.demo.bank.transaction.domain.model.FinancialTransaction;
import com.demo.bank.transaction.infrastructure.adapter.out.persistance.mapper.TransferMapper;
import com.demo.bank.transaction.infrastructure.adapter.out.persistance.repository.ReactiveTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements TransactionRepositoryPortOut {
    private final ReactiveTransactionRepository transactionRepository;
    private final TransferMapper transferMapper;

    @Override
    public Mono<FinancialTransaction> save(FinancialTransaction financialTransaction) {
        return transactionRepository.save(transferMapper.toEntity(financialTransaction))
                .map(transferMapper::toDomain);
    }
}
