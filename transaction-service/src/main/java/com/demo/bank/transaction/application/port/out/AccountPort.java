package com.demo.bank.transaction.application.port.out;

import com.demo.bank.transaction.application.dto.result.AccountOperationResult;
import com.demo.bank.transaction.application.dto.result.AccountValidationResult;
import com.demo.bank.transaction.domain.model.Money;
import com.demo.bank.transaction.infrastructure.adapter.out.http.account.AccountValidationResponse;
import reactor.core.publisher.Mono;

public interface AccountPort {
    Mono<AccountOperationResult> debit(Long id, Money money);
    Mono<AccountOperationResult> credit(Long id, Money money);
    Mono<AccountValidationResult> findAccountIdByNumber(String accountNumber);
    Mono<AccountValidationResult> findAccountById(Long id);
}
