CREATE TABLE account (
    id BIGINT AUTO_INCREMENT NOT NULL,
    account_number VARCHAR(14) NOT NULL,
    account_type VARCHAR(30) NOT NULL,
    balance DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    currency CHAR(3) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_account PRIMARY KEY (id),
    CONSTRAINT uk_account_account_number UNIQUE (account_number),
    CONSTRAINT chk_account_balance_non_negative CHECK (balance >= 0),
    CONSTRAINT chk_account_type CHECK (
        account_type IN ('CHECKING', 'SAVINGS')
    ),
    CONSTRAINT chk_account_currency CHECK (
        currency IN ('USD', 'PEN')
    ),
    CONSTRAINT chk_account_status CHECK (
        status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'CLOSED')
    )
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;