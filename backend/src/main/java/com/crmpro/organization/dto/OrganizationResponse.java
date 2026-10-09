package com.crmpro.organization.dto;

import com.crmpro.organization.entity.Organization;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class OrganizationResponse {
    private UUID id;
    private String name;
    private String legalName;
    private String document;
    private String email;
    private String phone;
    private String address;
    private String logoUrl;
    private String plan;
    private String status;
    private Instant createdAt;

    public static OrganizationResponse fromEntity(Organization org) {
        if (org == null) return null;
        return OrganizationResponse.builder()
                .id(org.getId())
                .name(org.getName())
                .legalName(org.getLegalName())
                .document(org.getDocument())
                .email(org.getEmail())
                .phone(org.getPhone())
                .address(org.getAddress())
                .logoUrl(org.getLogoUrl())
                .plan(org.getPlan())
                .status(org.getStatus())
                .createdAt(org.getCreatedAt())
                .build();
    }
}
