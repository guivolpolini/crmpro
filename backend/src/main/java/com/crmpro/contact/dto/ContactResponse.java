package com.crmpro.contact.dto;

import com.crmpro.contact.entity.Contact;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ContactResponse {
    private UUID id;
    private UUID organizationId;
    private UUID companyId;
    private String companyName;
    private String name;
    private String email;
    private String phone;
    private String jobTitle;
    private String notes;
    private String status;
    private Instant createdAt;

    public static ContactResponse fromEntity(Contact contact) {
        if (contact == null) return null;
        return ContactResponse.builder()
                .id(contact.getId())
                .organizationId(contact.getOrganizationId())
                .companyId(contact.getCompanyId())
                .companyName(contact.getCompany() != null ? contact.getCompany().getName() : null)
                .name(contact.getName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .jobTitle(contact.getJobTitle())
                .notes(contact.getNotes())
                .status(contact.getStatus())
                .createdAt(contact.getCreatedAt())
                .build();
    }
}
