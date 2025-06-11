-- liquibase formatted sql

-- changeset ilnaz:1
CREATE TABLE role(
    id BIGINT NOT NULL PRIMARY KEY,
    name varchar(20) NOT NULL UNIQUE
);

-- changeset ilnaz:2
CREATE SEQUENCE IF NOT EXISTS user_seq START WITH 1 INCREMENT BY 50;

-- changeset ilnaz:3
CREATE TABLE users(
    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('user_seq'),
    login VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(300) NOT NULL
);

-- changeset ilnaz:4
CREATE SEQUENCE IF NOT EXISTS client_seq START WITH 1 INCREMENT BY 50;

-- changeset ilnaz:5
CREATE TABLE client(
    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('client_seq'),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    middle_name VARCHAR(50),
    status VARCHAR(20),
    client_id BIGINT NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_user_client FOREIGN KEY (user_id) REFERENCES users(id)
);

-- changeset ilnaz:6
CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

-- changeset ilnaz:7
CREATE TABLE account(
    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('account_seq'),
    account_type VARCHAR(7) NOT NULL CHECK (account_type IN ('DEBIT', 'CREDIT')),
    balance DECIMAL(13, 2) NOT NULL DEFAULT 0.00,
    account_status VARCHAR(30) NOT NULL,
    account_id BIGINT NOT NULL UNIQUE,
    frozen_amount DECIMAL(13, 2) NOT NULL DEFAULT 0.00,
    client_id BIGINT NOT NULL,
    CONSTRAINT fk_account_client FOREIGN KEY (client_id) REFERENCES client(id)
);

-- changeset ilnaz:8
CREATE SEQUENCE IF NOT EXISTS transaction_seq START WITH 1 INCREMENT BY 50;

-- changeset ilnaz:9
CREATE TABLE financial_transaction(
    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('transaction_seq'),
    amount DECIMAL(13, 2) NOT NULL,
    transaction_time TIMESTAMP NOT NULL,
    transaction_status VARCHAR(30),
    transaction_id BIGINT NOT NULL UNIQUE,
    account_id BIGINT NOT NULL,
    CONSTRAINT fk_financial_transaction_account FOREIGN KEY (account_id) REFERENCES account(id)
);

-- changeset ilnaz:10
CREATE SEQUENCE IF NOT EXISTS data_error_log_seq START WITH 1 INCREMENT BY 50;

-- changeset ilnaz:11
CREATE TABLE data_source_error_log(
    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('data_error_log_seq'),
    stacktrace_text TEXT NOT NULL,
    message TEXT NOT NULL,
    method_signature TEXT NOT NULL
);

-- changeset ilnaz:12
CREATE SEQUENCE IF NOT EXISTS time_limit_exceed_log_seq START WITH 1 INCREMENT BY 50;

-- changeset ilnaz:13
CREATE TABLE time_limit_exceed_log(
    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('time_limit_exceed_log_seq'),
    exceed_time BIGINT NOT NULL,
    method_signature TEXT NOT NULL
);

-- changeset ilnaz:14
CREATE TABLE user_roles(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES role(id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id)
);