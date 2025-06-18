package com.example.task1.entity.enums;

import lombok.Getter;

@Getter
public enum Metrics {
    BLOCKED_CLIENT_COUNT("t1_java_blocked_clients_count"),
    ARRESTED_ACCOUNT_COUNT("t1_java_arrested_accounts_count");

    private final String value;

    Metrics(String name) {
        this.value = name;
    }
}