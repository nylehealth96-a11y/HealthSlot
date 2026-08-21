package com.hospital.smartqueue.hospital.api;
import com.hospital.smartqueue.hospital.application.HospitalStructureService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/hospitals")
public class HospitalController {
    private final HospitalStructureService service;
    public HospitalController(HospitalStructureService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public HospitalResponse create(@Valid @RequestBody CreateHospitalRequest request) { return HospitalResponse.from(service.createHospital(request.name())); }
    @GetMapping public List<HospitalResponse> list() { return service.listHospitals().stream().map(HospitalResponse::from).toList(); }
    @PostMapping("/{hospitalId}/branches") @ResponseStatus(HttpStatus.CREATED) public BranchResponse createBranch(@PathVariable UUID hospitalId, @Valid @RequestBody CreateBranchRequest request) { return BranchResponse.from(service.createBranch(hospitalId, request.name())); }
    @GetMapping("/{hospitalId}/branches") public List<BranchResponse> listBranches(@PathVariable UUID hospitalId) { return service.listBranches(hospitalId).stream().map(BranchResponse::from).toList(); }
}
