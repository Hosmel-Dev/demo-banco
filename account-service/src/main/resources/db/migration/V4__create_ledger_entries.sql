CREATE TABLE ledger_entries (
    id BIGINT AUTO_INCREMENT NOT NULL,
    account_id BIGINT NOT NULL,
    transaction_id BIGINT NOT NULL,          -- referencia lógica, SIN FK (es otro servicio)
    direction VARCHAR(10) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    currency CHAR(3) NOT NULL,
    balance_after DECIMAL(19,4) NOT NULL,    -- nuevo: saldo resultante
    entry_kind VARCHAR(20) NOT NULL DEFAULT 'ORIGINAL',
    reverses_entry_id BIGINT NULL,
    description VARCHAR(255) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_ledger_entries PRIMARY KEY (id),
    CONSTRAINT fk_ledger_entries_account
        FOREIGN KEY (account_id) REFERENCES account(id),
    CONSTRAINT fk_ledger_entries_reversed
        FOREIGN KEY (reverses_entry_id) REFERENCES ledger_entries(id),
    CONSTRAINT chk_ledger_direction CHECK (direction IN ('DEBIT','CREDIT')),
    CONSTRAINT chk_ledger_amount CHECK (amount > 0),
    CONSTRAINT chk_ledger_currency CHECK (currency IN ('USD','PEN')),
    CONSTRAINT chk_ledger_kind CHECK (entry_kind IN ('LEGACY','ORIGINAL','COMPENSATION')),

    -- Idempotencia: un reintento no puede duplicar el asiento
    CONSTRAINT uk_ledger_idempotency
        UNIQUE (transaction_id, account_id, direction, entry_kind),
    -- Una compensación solo puede revertir un asiento una vez
    CONSTRAINT uk_ledger_reverses UNIQUE (reverses_entry_id),

    INDEX idx_ledger_account_created (account_id, created_at),
    INDEX idx_ledger_transaction (transaction_id)
) ENGINE=InnoDB;