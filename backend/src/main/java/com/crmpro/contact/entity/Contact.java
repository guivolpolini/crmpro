package com.crmpro.contact.entity;

import com.crmpro.common.entity.TenantEntity;
import com.crmpro.company.entity.Company;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact extends TenantEntity {

    @Column(name = "company_id")
    private UUID companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

    @Column(nullable = false)
    private String name;

    private String email;

    private String phone;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    @Builder.Default
    private String status = "ACTIVE";
}
