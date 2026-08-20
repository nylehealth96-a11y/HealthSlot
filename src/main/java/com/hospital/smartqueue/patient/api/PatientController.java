package com.hospital.smartqueue.patient.api;

import com.hospital.smartqueue.patient.application.PatientProfileService;
import com.hospital.smartqueue.patient.application.PatientQueryService;
import com.hospital.smartqueue.patient.application.PatientRegistrationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {
    private final PatientRegistrationService registration;
    private final PatientQueryService queries;
    private final PatientProfileService profiles;

    public PatientController(PatientRegistrationService registration, PatientQueryService queries,
                             PatientProfileService profiles) {
        this.registration = registration;
        this.queries = queries;
        this.profiles = profiles;
    }

    @PostMapping
    public ResponseEntity<PatientResponse> create(@Valid @RequestBody CreatePatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(PatientResponse.from(registration.register(request.toCommand())));
    }

    @GetMapping("/{patientId}")
    public PatientResponse byId(@PathVariable UUID patientId) {
        return PatientResponse.from(queries.byId(patientId));
    }

    @GetMapping("/patient-number/{patientNumber}")
    public PatientResponse byPatientNumber(@PathVariable String patientNumber) {
        return PatientResponse.from(queries.byPatientNumber(patientNumber));
    }

    @GetMapping
    public Page<PatientResponse> search(@RequestParam @NotBlank String query,
                                        @RequestParam(defaultValue = "0") @Min(0) int page,
                                        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return queries.search(query, PageRequest.of(page, Math.min(size, 100))).map(PatientResponse::from);
    }

    @PutMapping("/{patientId}")
    public PatientResponse update(@PathVariable UUID patientId, @Valid @RequestBody UpdatePatientRequest request) {
        return PatientResponse.from(profiles.update(patientId, request.toCommand()));
    }
}
