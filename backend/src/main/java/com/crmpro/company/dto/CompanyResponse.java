package com.crmpro.company.dto;

import com.crmpro.company.entity.Company;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CompanyResponse {
    private UUID id;
    private UUID organizationId;
    private String name;
    private String tradeName;
    private String document;
    private String segment;
    private String website;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String state;
    private String status;
    private Instant createdAt;

    public static CompanyResponse fromEntity(Company company) {
        if (company == null) return null;
        return CompanyResponse.builder()
                .id(company.getId())
                .organizationId(company.getOrganizationId())
                .name(company.getName())
                .tradeName(company.getTradeName())
                .document(company.getDocument())
                .segment(company.getSegment())
                .website(company.getWebsite())
                .phone(company.getPhone())
                .email(company.getEmail())
                .address(company.getAddress())
                .city(company.getCity())
                .state(company.getState())
                .status(company.getStatus())
                .createdAt(company.getCreatedAt())
                .build();
    }
}
