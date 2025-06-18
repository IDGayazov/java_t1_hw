package com.example.starter.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "error.logging.db")
@Getter
@Setter
public class ErrorLoggingDbProperties {
    private boolean schemaValidation = true;
    private boolean autoCreateTables = true;
}
