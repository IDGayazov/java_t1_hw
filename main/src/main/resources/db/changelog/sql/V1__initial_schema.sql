-- liquibase formatted sql

-- changeset ilnaz:1
CREATE SEQUENCE IF NOT EXISTS client_seq START WITH 1 INCREMENT BY 50;

-- changeset ilnaz:2
CREATE TABLE client(
    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('client_seq'),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    middle_name VARCHAR(50),
    client_id BIGINT NOT NULL
);

-- changeset ilnaz:3
CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

-- changeset ilnaz:4
CREATE TABLE account(
    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('account_seq'),
    account_type VARCHAR(7) NOT NULL CHECK (account_type IN ('DEBIT', 'CREDIT')),
    balance DECIMAL(13, 2) NOT NULL DEFAULT 0.00,
    client_id BIGINT NOT NULL,
    CONSTRAINT fk_account_client FOREIGN KEY (client_id) REFERENCES client(id)
);

-- changeset ilnaz:5
CREATE SEQUENCE IF NOT EXISTS transaction_seq START WITH 1 INCREMENT BY 50;

-- changeset ilnaz:6
CREATE TABLE financial_transaction(
    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('transaction_seq'),
    amount DECIMAL(13, 2) NOT NULL,
    transaction_time TIMESTAMP NOT NULL,
    account_id BIGINT NOT NULL,
    CONSTRAINT fk_financial_transaction_account FOREIGN KEY (account_id) REFERENCES account(id)
);

-- changeset ilnaz:7
CREATE SEQUENCE IF NOT EXISTS data_error_log_seq START WITH 1 INCREMENT BY 50;

-- changeset ilnaz:8
CREATE TABLE data_source_error_log(
    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('data_error_log_seq'),
    stacktrace_text TEXT NOT NULL,
    message TEXT NOT NULL,
    method_signature TEXT NOT NULL
);

