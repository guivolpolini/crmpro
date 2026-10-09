package com.crmpro.proposal.dto;

import com.crmpro.proposal.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ProductResponse {
    private UUID id;
    private UUID organizationId;
    private String name;
    private String code;
    private String description;
    private BigDecimal unitPrice;
    private String unit;
    private boolean active;
    private Instant createdAt;

    public static ProductResponse fromEntity(Product p) {
        if (p == null) return null;
        return ProductResponse.builder()
                .id(p.getId())
                .organizationId(p.getOrganizationId())
                .name(p.getName())
                .code(p.getCode())
                .description(p.getDescription())
                .unitPrice(p.getUnitPrice())
                .unit(p.getUnit())
                .active(p.isActive())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
