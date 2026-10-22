CREATE TABLE identity_user (
    id BIGINT AUTO_INCREMENT NOT NULL,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    user_role VARCHAR(20) NOT NULL,

    CONSTRAINT pk_identity_user PRIMARY KEY (id),
    CONSTRAINT uk_identity_user_username UNIQUE (username),
    CONSTRAINT chk_identity_user_role
        CHECK (user_role IN ('CUSTOMER', 'OPERATOR'))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;