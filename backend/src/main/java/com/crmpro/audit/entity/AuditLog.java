package com.crmpro.audit.entity;

import com.crmpro.common.entity.TenantEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends TenantEntity {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "action", nullable = false, length = 50)
    private String action; // CREATE, UPDATE, DELETE, CONVERT, STAGE_TRANSITION

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType; // LEAD, DEAL, PROPOSAL, CONTACT, COMPANY

    @Column(name = "entity_id", length = 100)
    private String entityId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;
}
