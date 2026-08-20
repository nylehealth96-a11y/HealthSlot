package com.hospital.smartqueue.department.domain;
import jakarta.persistence.*; import java.util.UUID;
@Entity @Table(name="departments") public class Department { @Id private UUID id; @Column(name="branch_id") private UUID branchId; private String name; protected Department(){} public Department(UUID id,UUID branchId,String name){this.id=id;this.branchId=branchId;this.name=name;} public UUID getId(){return id;} public UUID getBranchId(){return branchId;} public String getName(){return name;} }
