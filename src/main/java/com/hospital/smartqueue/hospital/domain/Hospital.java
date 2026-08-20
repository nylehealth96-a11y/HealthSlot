package com.hospital.smartqueue.hospital.domain;
import jakarta.persistence.*; import java.util.UUID;
@Entity @Table(name="hospitals") public class Hospital { @Id private UUID id; private String name; protected Hospital(){} public Hospital(UUID id,String name){this.id=id;this.name=name;} public UUID getId(){return id;} public String getName(){return name;} }
