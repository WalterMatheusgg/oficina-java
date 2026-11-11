CREATE TABLE finance_transactions (
    id UUID PRIMARY KEY,
    description VARCHAR(120) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    type VARCHAR(16) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    category VARCHAR(24) NOT NULL CHECK (category IN
        ('FOOD', 'HOUSING', 'TRANSPORT', 'HEALTH', 'EDUCATION', 'LEISURE', 'SERVICES', 'OTHER')),
    occurred_on DATE NOT NULL
);

CREATE INDEX idx_finance_transactions_date ON finance_transactions (occurred_on);
CREATE INDEX idx_finance_transactions_category_date ON finance_transactions (category, occurred_on);

CREATE TABLE transaction_drafts (
    id UUID PRIMARY KEY,
    description VARCHAR(120) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    type VARCHAR(16) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    category VARCHAR(24) NOT NULL CHECK (category IN
        ('FOOD', 'HOUSING', 'TRANSPORT', 'HEALTH', 'EDUCATION', 'LEISURE', 'SERVICES', 'OTHER')),
    occurred_on DATE NOT NULL,
    transaction_id UUID UNIQUE REFERENCES finance_transactions(id),
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_transaction_drafts_created_at ON transaction_drafts (created_at);

CREATE TABLE monthly_budgets (
    id VARCHAR(40) PRIMARY KEY,
    budget_month VARCHAR(7) NOT NULL CHECK (budget_month ~ '^[0-9]{4}-(0[1-9]|1[0-2])$'),
    category VARCHAR(24) NOT NULL CHECK (category IN
        ('FOOD', 'HOUSING', 'TRANSPORT', 'HEALTH', 'EDUCATION', 'LEISURE', 'SERVICES', 'OTHER')),
    spending_limit NUMERIC(12, 2) NOT NULL CHECK (spending_limit > 0),
    CONSTRAINT uk_monthly_budgets_month_category UNIQUE (budget_month, category)
);
