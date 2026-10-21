ALTER TABLE financial_transactions
    ADD COLUMN origin_account_id BIGINT,
    ADD COLUMN destination_account_id BIGINT;

-- Recupera las cuentas de operaciones exitosas existentes.
-- En operaciones fallidas no siempre puede inferirse correctamente
-- porque puede haber asientos de compensación.
UPDATE financial_transactions ft
SET origin_account_id = (
    SELECT le.account_id
    FROM ledger_entries le
    WHERE le.transaction_id = ft.id
      AND le.direction = 'DEBIT'
    ORDER BY le.id
    LIMIT 1
)
WHERE ft.status = 'SUCCESS'
  AND ft.type IN ('WITHDRAWAL', 'TRANSFER');

UPDATE financial_transactions ft
SET destination_account_id = (
    SELECT le.account_id
    FROM ledger_entries le
    WHERE le.transaction_id = ft.id
      AND le.direction = 'CREDIT'
    ORDER BY le.id
    LIMIT 1
)
WHERE ft.status = 'SUCCESS'
  AND ft.type IN ('DEPOSIT', 'TRANSFER');

ALTER TABLE ledger_entries
    ADD COLUMN entry_kind VARCHAR(20) NOT NULL DEFAULT 'LEGACY',
    ADD COLUMN reverses_entry_id BIGINT,
    ADD COLUMN description VARCHAR(255);

-- Los registros anteriores quedan como LEGACY.
-- Los nuevos asientos serán originales si la aplicación no indica otra cosa.
ALTER TABLE ledger_entries
    ALTER COLUMN entry_kind SET DEFAULT 'ORIGINAL';

ALTER TABLE ledger_entries
    ADD CONSTRAINT chk_ledger_entries_kind
        CHECK (entry_kind IN ('LEGACY', 'ORIGINAL', 'COMPENSATION')),

    ADD CONSTRAINT fk_ledger_entries_reversed_entry
        FOREIGN KEY (reverses_entry_id)
        REFERENCES ledger_entries(id);

CREATE INDEX idx_financial_transactions_origin_account
    ON financial_transactions(origin_account_id);

CREATE INDEX idx_financial_transactions_destination_account
    ON financial_transactions(destination_account_id);

CREATE INDEX idx_ledger_entries_reverses_entry
    ON ledger_entries(reverses_entry_id);   