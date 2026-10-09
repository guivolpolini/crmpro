package com.crmpro.deal.entity;

import com.crmpro.common.entity.TenantEntity;
import com.crmpro.company.entity.Company;
import com.crmpro.contact.entity.Contact;
import com.crmpro.pipeline.entity.PipelineStage;
import com.crmpro.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "deals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deal extends TenantEntity {

    @Column(name = "stage_id", nullable = false)
    private UUID stageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", insertable = false, updatable = false)
    private PipelineStage stage;

    @Column(name = "company_id")
    private UUID companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

    @Column(name = "contact_id")
    private UUID contactId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", insertable = false, updatable = false)
    private Contact contact;

    @Column(name = "assigned_to_id")
    private UUID assignedToId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id", insertable = false, updatable = false)
    private User assignedTo;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Integer probability = 50;

    @Column(name = "expected_close_date")
    private LocalDate expectedCloseDate;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "lost_reason")
    private String lostReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DealStatus status = DealStatus.OPEN;
}
