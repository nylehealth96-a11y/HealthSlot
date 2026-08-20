package com.hospital.smartqueue.patient.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.smartqueue.common.infrastructure.AuditEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class PatientApiIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("patient_api").withUsername("patient").withPassword("patient");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private AuditEventRepository auditEvents;

    @Test
    void registersRetrievesSearchesAndUpdatesPatientWithoutChangingIdentifiers() throws Exception {
        String body = """
                {"firstName":"Ada","lastName":"Lovelace","dateOfBirth":"1990-01-01",
                 "gender":"FEMALE","mobileNumber":"+919876543210","email":"ada@example.test"}
                """;
        String createResponse = mvc.perform(post("/api/v1/patients").contentType("application/json").content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.patientNumber").value(org.hamcrest.Matchers.startsWith("PAT-")))
                .andReturn().getResponse().getContentAsString();
        JsonNode created = mapper.readTree(createResponse);
        String id = created.get("id").asText();
        String patientNumber = created.get("patientNumber").asText();

        mvc.perform(get("/api/v1/patients/{id}", id))
                .andExpect(status().isOk()).andExpect(jsonPath("$.patientNumber").value(patientNumber));
        mvc.perform(get("/api/v1/patients/patient-number/{number}", patientNumber.toLowerCase()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
        mvc.perform(get("/api/v1/patients").param("query", "Lovelace"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(id));
        mvc.perform(get("/api/v1/patients").param("query", patientNumber))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(id));
        mvc.perform(get("/api/v1/patients").param("query", "9876543210"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(id));
        mvc.perform(get("/api/v1/patients").param("query", "missing-value"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty());
        mvc.perform(get("/api/v1/patients").param("query", "Ada").param("size", "101"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        String createdAt = created.get("createdAt").asText();
        String update = """
                {"firstName":"Ada","lastName":"Byron","dateOfBirth":"1990-01-01",
                 "gender":"FEMALE","mobileNumber":"+919876543211"}
                """;
        String updateResponse = mvc.perform(put("/api/v1/patients/{id}", id).contentType("application/json").content(update))
                .andExpect(status().isOk()).andExpect(jsonPath("$.patientNumber").value(patientNumber))
                .andExpect(jsonPath("$.lastName").value("Byron")).andReturn().getResponse().getContentAsString();
        JsonNode updated = mapper.readTree(updateResponse);
        assertEquals(id, updated.get("id").asText());
        assertTrue(updated.get("updatedAt").asText().compareTo(createdAt) >= 0);
        assertEquals(1, auditEvents.countByActionAndTargetId("PATIENT_REGISTERED", java.util.UUID.fromString(id)));
        assertEquals(1, auditEvents.countByActionAndTargetId("PATIENT_PROFILE_UPDATED", java.util.UUID.fromString(id)));
    }

    @Test
    void rejectsInvalidRegistrationUsingGlobalErrorResponse() throws Exception {
        mvc.perform(post("/api/v1/patients").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void acceptsOmittedOptionalFieldsAndRejectsMalformedOrInvalidUpdateWithoutChangingProfile() throws Exception {
        String createResponse = mvc.perform(post("/api/v1/patients").contentType("application/json").content("""
                {"firstName":"Grace","lastName":"Hopper","dateOfBirth":"1980-01-01",
                 "gender":"FEMALE","mobileNumber":"+919876543299"}
                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.address").doesNotExist()).andReturn().getResponse().getContentAsString();
        String id = mapper.readTree(createResponse).get("id").asText();

        mvc.perform(post("/api/v1/patients").contentType("application/json").content("""
                {"firstName":"Bad","lastName":"Gender","dateOfBirth":"not-a-date",
                 "gender":"UNKNOWN","mobileNumber":"+919876543298"}
                """))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        mvc.perform(put("/api/v1/patients/{id}", id).contentType("application/json").content("""
                {"firstName":"Grace","lastName":"Changed","dateOfBirth":"1980-01-01",
                 "gender":"FEMALE","mobileNumber":"invalid"}
                """))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        mvc.perform(get("/api/v1/patients/{id}", id))
                .andExpect(status().isOk()).andExpect(jsonPath("$.lastName").value("Hopper"));
    }
}
