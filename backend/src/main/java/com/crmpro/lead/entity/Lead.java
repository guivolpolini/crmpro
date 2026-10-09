package com.crmpro.lead.entity;

import com.crmpro.common.entity.TenantEntity;
import com.crmpro.company.entity.Company;
import com.crmpro.contact.entity.Contact;
import com.crmpro.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "leads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead extends TenantEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "company_name")
    private String companyName;

    private String email;

    private String phone;

    @Column(nullable = false)
    @Builder.Default
    private String source = "WEBSITE";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LeadStatus status = LeadStatus.NEW;

    @Column(nullable = false)
    @Builder.Default
    private Integer score = 0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "assigned_to_id")
    private UUID assignedToId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id", insertable = false, updatable = false)
    private User assignedTo;

    @Column(name = "converted_at")
    private Instant convertedAt;

    @Column(name = "converted_contact_id")
    private UUID convertedContactId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "converted_contact_id", insertable = false, updatable = false)
    private Contact convertedContact;

    @Column(name = "converted_company_id")
    private UUID convertedCompanyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "converted_company_id", insertable = false, updatable = false)
    private Company convertedCompany;
}
