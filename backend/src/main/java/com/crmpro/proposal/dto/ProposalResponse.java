package com.crmpro.proposal.dto;

import com.crmpro.proposal.entity.Proposal;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ProposalResponse {
    private UUID id;
    private UUID organizationId;
    private UUID dealId;
    private String dealTitle;
    private String companyName;
    private String code;
    private BigDecimal totalAmount;
    private BigDecimal discount;
    private String status;
    private LocalDate validUntil;
    private String notes;
    private Instant createdAt;

    public static ProposalResponse fromEntity(Proposal prop) {
        if (prop == null) return null;
        return ProposalResponse.builder()
                .id(prop.getId())
                .organizationId(prop.getOrganizationId())
                .dealId(prop.getDealId())
                .dealTitle(prop.getDeal() != null ? prop.getDeal().getTitle() : null)
                .companyName(prop.getDeal() != null && prop.getDeal().getCompany() != null ? prop.getDeal().getCompany().getName() : null)
                .code(prop.getCode())
                .totalAmount(prop.getTotalAmount())
                .discount(prop.getDiscount())
                .status(prop.getStatus())
                .validUntil(prop.getValidUntil())
                .notes(prop.getNotes())
                .createdAt(prop.getCreatedAt())
                .build();
    }
}
