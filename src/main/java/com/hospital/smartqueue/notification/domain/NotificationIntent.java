package com.hospital.smartqueue.notification.domain;

import com.hospital.smartqueue.notification.application.port.NotificationTriggerPort;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="notification_intents", uniqueConstraints=@UniqueConstraint(name="uq_notification_source_type", columnNames={"source_event_id","notification_type"}))
public class NotificationIntent {
 @Id @GeneratedValue @UuidGenerator private UUID id;
 @Column(name="source_event_id",nullable=false,updatable=false) private UUID sourceEventId;
 @Column(name="notification_type",nullable=false,updatable=false) @Enumerated(EnumType.STRING) private NotificationTriggerPort.NotificationType type;
 @Column(name="hospital_id",nullable=false,updatable=false) private UUID hospitalId;
 @Column(name="branch_id",nullable=false,updatable=false) private UUID branchId;
 @Column(name="appointment_or_visit_id",nullable=false,updatable=false) private UUID appointmentOrVisitId;
 @Enumerated(EnumType.STRING) @Column(nullable=false,updatable=false) private NotificationTriggerPort.RecipientClass recipientClass;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private NotificationStatus status=NotificationStatus.PENDING;
 @Column(nullable=false) private int attemptCount;
 @Column(nullable=false,updatable=false) private Instant createdAt;
 @Column(nullable=false) private Instant updatedAt;
 protected NotificationIntent(){}
 public NotificationIntent(NotificationTriggerPort.NotificationTrigger t, Instant now){sourceEventId=t.sourceEventId();type=t.type();hospitalId=t.hospitalId();branchId=t.branchId();appointmentOrVisitId=t.appointmentOrVisitId();recipientClass=t.recipientClass();createdAt=now;updatedAt=now;}
 @PreUpdate void touch(){updatedAt=Instant.now();}
 public UUID getId(){return id;} public NotificationStatus getStatus(){return status;} public int getAttemptCount(){return attemptCount;}
 public boolean claim(){if(status!=NotificationStatus.PENDING)return false;status=NotificationStatus.DELIVERING;attemptCount++;return true;}
 public void delivered(){status=NotificationStatus.DELIVERED;} public void failed(){status=attemptCount>=4?NotificationStatus.FAILED:NotificationStatus.PENDING;} public void suppress(){if(status==NotificationStatus.PENDING||status==NotificationStatus.DELIVERING)status=NotificationStatus.SUPPRESSED;}
}
