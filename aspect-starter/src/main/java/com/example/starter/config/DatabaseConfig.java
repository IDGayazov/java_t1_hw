package com.example.starter.config;

import com.example.starter.config.property.ErrorLoggingDbProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
@ConditionalOnClass(DataSource.class)
@ConditionalOnProperty(name = "error.logging.db.enabled", havingValue = "true")
@EnableConfigurationProperties(ErrorLoggingDbProperties.class)
public class DatabaseConfig {

    private final DataSource dataSource;
    private final ErrorLoggingDbProperties properties;

    public DatabaseConfig(DataSource dataSource,
                          ErrorLoggingDbProperties properties) {
        this.dataSource = dataSource;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void validateAndCreateSchema() {
        if (properties.isSchemaValidation()) {
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                String schema = connection.getSchema();

                validateAndCreateSequence(connection, metaData, schema, "data_error_log_seq");
                validateAndCreateSequence(connection, metaData, schema, "time_limit_exceed_log_seq");

                validateAndCreateTable(connection, metaData, schema,
                        "time_limit_exceed_log", getTimeLimitExceedTableSql(metaData));

                validateAndCreateTable(connection, metaData, schema,
                        "data_source_error_log", getDataSourceErrorTableSql(metaData));

            } catch (SQLException e) {
                throw new RuntimeException("Database validation failed", e);
            }
        }
    }

    private void validateAndCreateSequence(Connection connection,
                                           DatabaseMetaData metaData,
                                           String schema,
                                           String sequenceName) throws SQLException {
        if (!isSequenceExists(metaData, schema, sequenceName)) {
            if (properties.isAutoCreateTables()) {
                createSequence(connection, sequenceName);
            } else {
                throw new IllegalStateException(
                        "Required sequence " + sequenceName + " does not exist");
            }
        }
    }

    private boolean isSequenceExists(DatabaseMetaData metaData,
                                     String schema,
                                     String sequenceName) throws SQLException {
        try (ResultSet sequences = metaData.getTables(null, schema, sequenceName,
                new String[] {"SEQUENCE"})) {
            return sequences.next();
        }
    }

    private void createSequence(Connection connection, String sequenceName) throws SQLException {
        String sql = "CREATE SEQUENCE " + sequenceName + " START WITH 1 INCREMENT BY 1";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private void validateAndCreateTable(Connection connection,
                                        DatabaseMetaData metaData,
                                        String schema,
                                        String tableName,
                                        String createSql) throws SQLException {
        if (!isTableExists(metaData, schema, tableName)) {
            if (properties.isAutoCreateTables()) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute(createSql);
                }
            } else {
                throw new IllegalStateException(
                        "Required table " + tableName + " does not exist");
            }
        }
    }

    private boolean isTableExists(DatabaseMetaData metaData,
                                  String schema,
                                  String tableName) throws SQLException {
        try (ResultSet tables = metaData.getTables(null, schema, tableName, null)) {
            return tables.next();
        }
    }

    private String getTimeLimitExceedTableSql(DatabaseMetaData metaData) throws SQLException {
        String dbProductName = metaData.getDatabaseProductName();
        if (dbProductName.equals("PostgreSQL")) {
            return """
                    CREATE TABLE time_limit_exceed_log(
                        id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('time_limit_exceed_log_seq'),
                        exceed_time BIGINT NOT NULL,
                        method_signature TEXT NOT NULL
                    )""";
        }
        throw new UnsupportedOperationException(
                "Unsupported database: " + dbProductName);
    }

    private String getDataSourceErrorTableSql(DatabaseMetaData metaData) throws SQLException {
        String dbProductName = metaData.getDatabaseProductName();
        if (dbProductName.equals("PostgreSQL")) {
            return """
                    CREATE TABLE data_source_error_log(
                        id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('data_error_log_seq'),
                        stacktrace_text TEXT NOT NULL,
                        message TEXT NOT NULL,
                        method_signature TEXT NOT NULL
                    )""";
        }
        throw new UnsupportedOperationException(
                "Unsupported database: " + dbProductName);
    }
}
