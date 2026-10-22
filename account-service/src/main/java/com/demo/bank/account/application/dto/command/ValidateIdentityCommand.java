package com.demo.bank.account.application.dto.command;

public record ValidateIdentityCommand(
        String username,
        String password
) {
}
