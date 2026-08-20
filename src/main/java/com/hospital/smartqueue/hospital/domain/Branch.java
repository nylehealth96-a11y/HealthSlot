package com.hospital.smartqueue.hospital.domain;
import jakarta.persistence.*; import java.util.UUID;
@Entity @Table(name="branches") public class Branch { @Id private UUID id; @Column(name="hospital_id") private UUID hospitalId; private String name; protected Branch(){} public Branch(UUID id,UUID hospitalId,String name){this.id=id;this.hospitalId=hospitalId;this.name=name;} public UUID getId(){return id;} public UUID getHospitalId(){return hospitalId;} public String getName(){return name;} }
