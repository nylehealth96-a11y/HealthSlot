package com.hospital.smartqueue.patient.integration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
class PatientMigrationIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("patient_test").withUsername("patient").withPassword("patient");

    @Test
    void migrationCreatesPatientTableAndEnforcesPatientNumberUniqueness() throws Exception {
        Flyway.configure().dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword()).load().migrate();
        try (var connection = DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
             var statement = connection.createStatement()) {
            statement.executeUpdate("insert into patients (id, patient_number, first_name, last_name, date_of_birth, gender, mobile_number) values ('00000000-0000-0000-0000-000000000001', 'PAT-1', 'Ada', 'Lovelace', '1990-01-01', 'FEMALE', '+919876543210')");
            assertEquals(1, statement.executeUpdate("insert into patients (id, patient_number, first_name, last_name, date_of_birth, gender, mobile_number) values ('00000000-0000-0000-0000-000000000002', 'PAT-2', 'Grace', 'Hopper', '1980-01-01', 'FEMALE', '+919876543211')"));
            assertThrows(SQLException.class, () -> statement.executeUpdate("insert into patients (id, patient_number, first_name, last_name, date_of_birth, gender, mobile_number) values ('00000000-0000-0000-0000-000000000003', 'PAT-1', 'Alan', 'Turing', '1985-01-01', 'MALE', '+919876543212')"));
        }
    }
}
