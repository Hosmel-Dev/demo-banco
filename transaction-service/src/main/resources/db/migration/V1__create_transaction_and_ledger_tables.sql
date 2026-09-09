CREATE TABLE financial_transactions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    idempotency_key VARCHAR(100) NOT NULL,
    type VARCHAR(30) NOT NULL,
    amount NUMERIC(19,4) NOT NULL,
    currency CHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_financial_transactions_idempotency_key
        UNIQUE (idempotency_key),

    CONSTRAINT chk_financial_transactions_type CHECK (
        type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')
    ),

    CONSTRAINT chk_financial_transactions_amount CHECK (
        amount > 0
    ),

    CONSTRAINT chk_financial_transactions_currency CHECK (
        currency IN ('USD', 'PEN')
    ),

    CONSTRAINT chk_financial_transactions_status CHECK (
        status IN ('PENDING', 'SUCCESS', 'FAILED', 'DECLINED', 'CANCELLED')
    )
);

CREATE TABLE ledger_entries (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    direction VARCHAR(10) NOT NULL,
    amount NUMERIC(19,4) NOT NULL,
    currency CHAR(3) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ledger_entries_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES financial_transactions(id),

    CONSTRAINT chk_ledger_entries_direction CHECK (
        direction IN ('DEBIT', 'CREDIT')
    ),

    CONSTRAINT chk_ledger_entries_amount CHECK (
        amount > 0
    ),

    CONSTRAINT chk_ledger_entries_currency CHECK (
        currency IN ('USD', 'PEN')
    )
);

CREATE INDEX idx_ledger_entries_transaction_id
    ON ledger_entries (transaction_id);

CREATE INDEX idx_ledger_entries_account_id
    ON ledger_entries (account_id);