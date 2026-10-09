package com.crmpro.lead.dto;

import com.crmpro.lead.entity.Lead;
import com.crmpro.lead.entity.LeadStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class LeadResponse {
    private UUID id;
    private UUID organizationId;
    private String name;
    private String companyName;
    private String email;
    private String phone;
    private String source;
    private LeadStatus status;
    private Integer score;
    private String notes;
    private UUID assignedToId;
    private String assignedToName;
    private Instant convertedAt;
    private UUID convertedContactId;
    private UUID convertedCompanyId;
    private Instant createdAt;

    public static LeadResponse fromEntity(Lead lead) {
        if (lead == null) return null;
        return LeadResponse.builder()
                .id(lead.getId())
                .organizationId(lead.getOrganizationId())
                .name(lead.getName())
                .companyName(lead.getCompanyName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .source(lead.getSource())
                .status(lead.getStatus())
                .score(lead.getScore())
                .notes(lead.getNotes())
                .assignedToId(lead.getAssignedToId())
                .assignedToName(lead.getAssignedTo() != null ? lead.getAssignedTo().getName() : null)
                .convertedAt(lead.getConvertedAt())
                .convertedContactId(lead.getConvertedContactId())
                .convertedCompanyId(lead.getConvertedCompanyId())
                .createdAt(lead.getCreatedAt())
                .build();
    }
}
