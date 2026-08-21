package com.hospital.smartqueue.hospital.api;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record CreateHospitalRequest(@NotBlank @Size(max = 200) String name) { }
