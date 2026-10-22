package com.demo.bank.account.application.dto.result;

import com.demo.bank.account.domain.enums.UserRole;

public record ValidateIdentityResult(
        String username,
        UserRole role,
        String token
) {
}
