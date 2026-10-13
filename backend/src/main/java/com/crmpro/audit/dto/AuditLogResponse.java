package com.crmpro.audit.dto;

import com.crmpro.audit.entity.AuditLog;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class AuditLogResponse {
    private UUID id;
    private UUID organizationId;
    private UUID userId;
    private String userEmail;
    private String action;
    private String entityType;
    private String entityId;
    private String description;
    private String ipAddress;
    private Instant createdAt;

    public static AuditLogResponse fromEntity(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .organizationId(log.getOrganizationId())
                .userId(log.getUserId())
                .userEmail(log.getUserEmail())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .description(log.getDescription())
                .ipAddress(log.getIpAddress())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
