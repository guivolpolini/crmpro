package com.crmpro.deal.dto;

import com.crmpro.deal.entity.Deal;
import com.crmpro.deal.entity.DealStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class DealResponse {
    private UUID id;
    private UUID organizationId;
    private UUID stageId;
    private String stageName;
    private String stageColor;
    private UUID companyId;
    private String companyName;
    private UUID contactId;
    private String contactName;
    private UUID assignedToId;
    private String assignedToName;
    private String title;
    private BigDecimal amount;
    private Integer probability;
    private LocalDate expectedCloseDate;
    private Instant closedAt;
    private String lostReason;
    private DealStatus status;
    private Instant createdAt;

    public static DealResponse fromEntity(Deal deal) {
        if (deal == null) return null;
        return DealResponse.builder()
                .id(deal.getId())
                .organizationId(deal.getOrganizationId())
                .stageId(deal.getStageId())
                .stageName(deal.getStage() != null ? deal.getStage().getName() : null)
                .stageColor(deal.getStage() != null ? deal.getStage().getColor() : null)
                .companyId(deal.getCompanyId())
                .companyName(deal.getCompany() != null ? deal.getCompany().getName() : null)
                .contactId(deal.getContactId())
                .contactName(deal.getContact() != null ? deal.getContact().getName() : null)
                .assignedToId(deal.getAssignedToId())
                .assignedToName(deal.getAssignedTo() != null ? deal.getAssignedTo().getName() : null)
                .title(deal.getTitle())
                .amount(deal.getAmount())
                .probability(deal.getProbability())
                .expectedCloseDate(deal.getExpectedCloseDate())
                .closedAt(deal.getClosedAt())
                .lostReason(deal.getLostReason())
                .status(deal.getStatus())
                .createdAt(deal.getCreatedAt())
                .build();
    }
}
