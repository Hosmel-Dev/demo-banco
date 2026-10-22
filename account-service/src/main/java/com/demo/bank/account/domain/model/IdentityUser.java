package com.demo.bank.account.domain.model;

import com.demo.bank.account.domain.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentityUser {
    private Long id;
    private String username;
    private String passwordHash;
    private UserRole role;
}
