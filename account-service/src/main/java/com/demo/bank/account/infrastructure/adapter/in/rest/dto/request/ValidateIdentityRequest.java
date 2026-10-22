package com.demo.bank.account.infrastructure.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ValidateIdentityRequest (
        @NotBlank
        String username,
        @NotBlank
        String password
){
}
