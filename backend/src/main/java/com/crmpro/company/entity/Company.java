package com.crmpro.company.entity;

import com.crmpro.common.entity.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends TenantEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "trade_name")
    private String tradeName;

    private String document;

    private String segment;

    private String website;

    private String phone;

    private String email;

    private String address;

    private String city;

    private String state;

    @Column(nullable = false)
    @Builder.Default
    private String status = "ACTIVE";
}
