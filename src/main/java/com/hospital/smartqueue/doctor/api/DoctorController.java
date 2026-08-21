package com.hospital.smartqueue.doctor.api;
import com.hospital.smartqueue.doctor.application.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/hospitals/{hospitalId}")
public class DoctorController {
    private final DoctorService service;
    public DoctorController(DoctorService service) { this.service = service; }
    @PostMapping("/doctors") @ResponseStatus(HttpStatus.CREATED) public DoctorResponse register(@PathVariable UUID hospitalId, @Valid @RequestBody RegisterDoctorRequest request) { return DoctorResponse.from(service.register(hospitalId, request.doctorCode(), request.name(), request.specialization(), request.professionalRegistrationNumber(), request.status(), request.departmentIds())); }
    @GetMapping("/doctors") public List<DoctorResponse> list(@PathVariable UUID hospitalId) { return service.list(hospitalId).stream().map(DoctorResponse::from).toList(); }
    @GetMapping("/departments/{departmentId}/doctors") public List<DoctorResponse> listByDepartment(@PathVariable UUID hospitalId, @PathVariable UUID departmentId) { return service.listByDepartment(hospitalId, departmentId).stream().map(DoctorResponse::from).toList(); }
    @PatchMapping("/doctors/{doctorId}/status") public DoctorResponse setStatus(@PathVariable UUID hospitalId, @PathVariable UUID doctorId, @Valid @RequestBody DoctorStatusRequest request) { return DoctorResponse.from(service.setStatus(hospitalId, doctorId, request.status())); }
}
