package com.crmpro.proposal.entity;

import com.crmpro.common.entity.TenantEntity;
import com.crmpro.deal.entity.Deal;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "proposals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proposal extends TenantEntity {

    @Column(name = "deal_id", nullable = false)
    private UUID dealId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_id", insertable = false, updatable = false)
    private Deal deal;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "DRAFT"; // DRAFT, SENT, ACCEPTED, REJECTED

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
