package com.demo.bank.account.infrastructure.adapter.in.rest.dto.response;

import com.demo.bank.account.domain.enums.UserRole;

public record ValidateIdentityResponse (
        String username,
        UserRole role
){
}
