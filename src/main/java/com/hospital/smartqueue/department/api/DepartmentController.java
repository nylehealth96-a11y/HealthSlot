package com.hospital.smartqueue.department.api;
import com.hospital.smartqueue.department.application.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/hospitals/{hospitalId}/branches/{branchId}/departments")
public class DepartmentController {
    private final DepartmentService service;
    public DepartmentController(DepartmentService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public DepartmentResponse create(@PathVariable UUID hospitalId, @PathVariable UUID branchId, @Valid @RequestBody CreateDepartmentRequest request) { return DepartmentResponse.from(service.create(hospitalId, branchId, request.name())); }
    @GetMapping public List<DepartmentResponse> list(@PathVariable UUID hospitalId, @PathVariable UUID branchId) { return service.list(hospitalId, branchId).stream().map(DepartmentResponse::from).toList(); }
}
