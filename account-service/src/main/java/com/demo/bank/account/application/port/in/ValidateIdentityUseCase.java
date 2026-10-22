package com.demo.bank.account.application.port.in;

import com.demo.bank.account.application.dto.command.ValidateIdentityCommand;
import com.demo.bank.account.application.dto.result.ValidateIdentityResult;
import com.demo.bank.account.domain.model.IdentityUser;
import reactor.core.publisher.Mono;

public interface ValidateIdentityUseCase {
    Mono<ValidateIdentityResult> execute(ValidateIdentityCommand validateIdentityCommand);
}
