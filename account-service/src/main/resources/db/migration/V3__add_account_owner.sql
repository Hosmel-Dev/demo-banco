ALTER TABLE account
    ADD COLUMN owner_user_id BIGINT NULL,
    ADD INDEX idx_account_owner_user_id (owner_user_id),
    ADD CONSTRAINT fk_account_owner_user
        FOREIGN KEY (owner_user_id)
        REFERENCES identity_user(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT;