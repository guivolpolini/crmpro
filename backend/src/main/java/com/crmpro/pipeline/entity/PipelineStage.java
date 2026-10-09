package com.crmpro.pipeline.entity;

import com.crmpro.common.entity.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "pipeline_stages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PipelineStage extends TenantEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "stage_order", nullable = false)
    @Builder.Default
    private Integer stageOrder = 0;

    @Column(length = 30)
    @Builder.Default
    private String color = "#3b82f6";

    @Column(name = "is_won", nullable = false)
    @Builder.Default
    private boolean won = false;

    @Column(name = "is_lost", nullable = false)
    @Builder.Default
    private boolean lost = false;
}
