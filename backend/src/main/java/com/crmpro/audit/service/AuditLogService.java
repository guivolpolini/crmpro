package com.crmpro.audit.service;

import com.crmpro.audit.dto.AuditLogResponse;
import com.crmpro.audit.entity.AuditLog;
import com.crmpro.audit.repository.AuditLogRepository;
import com.crmpro.common.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(UUID organizationId, String action, String entityType, String entityId, String description) {
        try {
            if (organizationId == null) {
                organizationId = TenantContext.getTenantId();
            }
            if (organizationId == null) {
                log.warn("Tentativa de auditoria ignorada: organizationId nulo");
                return;
            }

            String userEmail = "system";
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                userEmail = auth.getName();
            }

            AuditLog auditLog = AuditLog.builder()
                    .userEmail(userEmail)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .description(description)
                    .build();
            auditLog.setOrganizationId(organizationId);

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Erro ao registrar log de auditoria: {}", e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogs(int page, int size) {
        UUID organizationId = TenantContext.getTenantId();
        return auditLogRepository.findAllByOrganizationIdOrderByCreatedAtDesc(
                organizationId,
                PageRequest.of(page, size)
        ).map(AuditLogResponse::fromEntity);
    }
}
