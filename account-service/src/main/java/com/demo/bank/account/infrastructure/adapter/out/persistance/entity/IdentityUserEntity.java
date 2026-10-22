package com.demo.bank.account.infrastructure.adapter.out.persistance.entity;

import com.demo.bank.account.domain.enums.UserRole;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("identity_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
public class IdentityUserEntity {
    @Id
    @Column("id")
    private Long id;
    @Column("username")
    private String username;
    @Column("password_hash")
    private String passwordHash;
    @Column("user_role")
    private UserRole role;
}
