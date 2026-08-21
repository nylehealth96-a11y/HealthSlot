package com.hospital.smartqueue.department.api;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record CreateDepartmentRequest(@NotBlank @Size(max = 200) String name) { }
