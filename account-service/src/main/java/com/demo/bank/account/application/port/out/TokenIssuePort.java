package com.demo.bank.account.application.port.out;

import com.demo.bank.account.domain.enums.UserRole;
import reactor.core.publisher.Mono;

public interface TokenIssuePort {
    Mono<String> issue(Long userId, String username, UserRole role);
}
