package com.crmpro.proposal.entity;

import com.crmpro.common.entity.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends TenantEntity {

    @Column(nullable = false)
    private String name;

    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(length = 50)
    @Builder.Default
    private String unit = "UN";

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
