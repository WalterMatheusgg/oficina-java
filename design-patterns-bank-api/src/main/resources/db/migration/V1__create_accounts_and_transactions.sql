CREATE TABLE accounts (
    account_id VARCHAR(64) PRIMARY KEY,
    balance NUMERIC(19, 2) NOT NULL CHECK (balance >= 0),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE transactions (
    transaction_id UUID PRIMARY KEY,
    account_id VARCHAR(64) NOT NULL REFERENCES accounts (account_id),
    idempotency_key VARCHAR(128) NOT NULL,
    operation_type VARCHAR(20) NOT NULL CHECK (operation_type IN ('DEPOSIT', 'WITHDRAWAL')),
    amount NUMERIC(19, 2) NOT NULL CHECK (amount > 0),
    balance_after NUMERIC(19, 2) NOT NULL CHECK (balance_after >= 0),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_transactions_account_idempotency UNIQUE (account_id, idempotency_key)
);

CREATE INDEX idx_transactions_account_created_at
    ON transactions (account_id, created_at DESC);

INSERT INTO accounts (account_id, balance) VALUES
    ('ACC-1001', 1000.00),
    ('ACC-1002', 250.00);
