package com.demo.bank.account.application.service;

import com.demo.bank.account.application.dto.command.ValidateIdentityCommand;
import com.demo.bank.account.application.dto.result.ValidateIdentityResult;
import com.demo.bank.account.application.port.in.ValidateIdentityUseCase;
import com.demo.bank.account.application.port.out.IdentityRepositoryPortOut;
import com.demo.bank.account.application.port.out.PasswordVerifierPort;
import com.demo.bank.account.application.port.out.TokenIssuePort;
import com.demo.bank.account.domain.exception.UnauthorizedUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ValidateIdentity implements ValidateIdentityUseCase {
    private final IdentityRepositoryPortOut identityRepositoryPortOut;
    private final PasswordVerifierPort passwordVerifierPort;
    private final TokenIssuePort tokenIssuePort;

    @Override
    public Mono<ValidateIdentityResult> execute(ValidateIdentityCommand validateIdentityCommand) {
        var password = validateIdentityCommand.password();
        var username = validateIdentityCommand.username();

        return identityRepositoryPortOut.findByUsername(username)
                .switchIfEmpty(Mono.error(new UnauthorizedUserException()))
                .flatMap(user -> passwordVerifierPort
                        .matches(password,user.getPasswordHash())
                        .flatMap(valid -> {
                            if (!valid) {
                                return Mono.error(new UnauthorizedUserException());
                            }
                            return tokenIssuePort.issue(
                                    user.getId(),
                                    user.getUsername(),
                                    user.getRole())
                                    .map(
                                            token -> new ValidateIdentityResult(
                                                    user.getUsername(),
                                                    user.getRole(),
                                                    token)
                                    );
                        })
                );
    }
}
