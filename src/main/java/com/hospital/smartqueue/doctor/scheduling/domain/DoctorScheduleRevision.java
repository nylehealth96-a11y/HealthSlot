package com.hospital.smartqueue.doctor.scheduling.domain;
import jakarta.persistence.*; import org.hibernate.annotations.UuidGenerator; import java.time.*; import java.util.*;
@Entity @Table(name="doctor_schedule_revisions") public class DoctorScheduleRevision {
 @Id @GeneratedValue @UuidGenerator private UUID id; @Column(name="doctor_id",nullable=false) private UUID doctorId; @Column(name="branch_id",nullable=false) private UUID branchId; @Column(name="effective_from",nullable=false) private LocalDate effectiveFrom; @Column(name="slot_duration_minutes",nullable=false) private int slotDurationMinutes; @Version private long version;
 @OneToMany(mappedBy="schedule",cascade=CascadeType.ALL,orphanRemoval=true) private List<WorkingPeriod> periods=new ArrayList<>(); protected DoctorScheduleRevision(){}
 public DoctorScheduleRevision(UUID doctorId,UUID branchId,LocalDate effectiveFrom,int duration){this.doctorId=doctorId;this.branchId=branchId;this.effectiveFrom=effectiveFrom;this.slotDurationMinutes=duration;}
 public UUID getId(){return id;} public UUID getDoctorId(){return doctorId;} public UUID getBranchId(){return branchId;} public LocalDate getEffectiveFrom(){return effectiveFrom;} public int getSlotDurationMinutes(){return slotDurationMinutes;} public long getVersion(){return version;} public List<WorkingPeriod> getPeriods(){return List.copyOf(periods);} public void add(WorkingPeriod p){periods.add(p);}
}
