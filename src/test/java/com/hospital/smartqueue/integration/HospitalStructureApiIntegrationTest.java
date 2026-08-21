package com.hospital.smartqueue.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class HospitalStructureApiIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("healthslot").withUsername("healthslot").withPassword("healthslot");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JdbcTemplate jdbcTemplate;

    @Test
    void managesHierarchyDoctorsStatusAndHospitalIsolation() throws Exception {
        String hospitalA = id(postJson("/api/v1/hospitals", "{\"name\":\"North Hospital\"}"));
        String hospitalB = id(postJson("/api/v1/hospitals", "{\"name\":\"South Hospital\"}"));
        mockMvc.perform(post("/api/v1/hospitals").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\" north hospital \"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("CONFLICT"));
        assertConcurrentDuplicateCreationIsRejected();

        String branchA = id(postJson("/api/v1/hospitals/" + hospitalA + "/branches", "{\"name\":\"Central\"}"));
        String departmentA = id(postJson("/api/v1/hospitals/" + hospitalA + "/branches/" + branchA + "/departments", "{\"name\":\"Cardiology\"}"));
        String branchB = id(postJson("/api/v1/hospitals/" + hospitalB + "/branches", "{\"name\":\"Central\"}"));
        String departmentB = id(postJson("/api/v1/hospitals/" + hospitalB + "/branches/" + branchB + "/departments", "{\"name\":\"Cardiology\"}"));

        String doctor = id(postJson("/api/v1/hospitals/" + hospitalA + "/doctors", "{\"doctorCode\":\"DOC-001\",\"name\":\"Dr Ada\",\"specialization\":\"Cardiology\",\"professionalRegistrationNumber\":\"REG-1\",\"departmentIds\":[\"" + departmentA + "\"]}"));
        mockMvc.perform(get("/api/v1/hospitals/" + hospitalA + "/departments/" + departmentA + "/doctors"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(doctor));
        mockMvc.perform(post("/api/v1/hospitals/" + hospitalB + "/doctors").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doctorCode\":\"DOC-OTHER\",\"name\":\"Dr Other\",\"specialization\":\"Cardiology\",\"professionalRegistrationNumber\":\" reg-1 \",\"departmentIds\":[\"" + departmentB + "\"]}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("CONFLICT"));
        mockMvc.perform(patch("/api/v1/hospitals/" + hospitalA + "/doctors/" + doctor + "/status").contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("INACTIVE"));
        mockMvc.perform(get("/api/v1/hospitals/" + hospitalA + "/doctors"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].status").value("INACTIVE"));
        mockMvc.perform(post("/api/v1/hospitals/" + hospitalA + "/doctors").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doctorCode\":\"DOC-002\",\"name\":\"Dr B\",\"specialization\":\"Cardiology\",\"professionalRegistrationNumber\":\"REG-2\",\"departmentIds\":[\"" + departmentB + "\"]}"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("NOT_FOUND"));
        mockMvc.perform(get("/api/v1/hospitals/" + hospitalB + "/departments/" + departmentA + "/doctors"))
                .andExpect(status().isNotFound());
        assertThat(jdbcTemplate.queryForObject("select count(*) from audit_events", Integer.class)).isGreaterThanOrEqualTo(6);
    }

    private void assertConcurrentDuplicateCreationIsRejected() throws Exception {
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Callable<Integer> request = () -> {
                ready.countDown();
                start.await();
                return mockMvc.perform(post("/api/v1/hospitals").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Concurrent Hospital\"}"))
                        .andReturn().getResponse().getStatus();
            };
            Future<Integer> first = pool.submit(request);
            Future<Integer> second = pool.submit(request);
            ready.await();
            start.countDown();
            List<Integer> statuses = List.of(first.get(), second.get());
            assertThat(statuses).contains(201, 409);
            assertThat(jdbcTemplate.queryForObject("select count(*) from hospitals where canonical_name = 'concurrent hospital'", Integer.class)).isEqualTo(1);
        } finally {
            pool.shutdownNow();
        }
    }

    private String id(org.springframework.test.web.servlet.ResultActions action) throws Exception {
        String body = action.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        JsonNode response = objectMapper.readTree(body);
        return response.get("id").asText();
    }

    private org.springframework.test.web.servlet.ResultActions postJson(String path, String body) throws Exception {
        return mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(body));
    }
}
