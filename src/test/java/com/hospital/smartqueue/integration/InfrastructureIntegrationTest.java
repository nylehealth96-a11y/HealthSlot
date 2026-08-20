package com.hospital.smartqueue.integration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InfrastructureIntegrationTest extends PostgresIntegrationTest {
    @Test
    void migrationCreatesFoundationalTables() throws Exception {
        Flyway.configure().dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword()).load().migrate();
        try (var connection = DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
             var statement = connection.prepareStatement("select count(*) from information_schema.tables where table_schema = 'public' and table_name in ('hospitals', 'branches', 'departments', 'doctors', 'doctor_departments', 'audit_events')");
             var result = statement.executeQuery()) {
            result.next();
            assertTrue(result.getInt(1) == 6);
        }
    }
}
