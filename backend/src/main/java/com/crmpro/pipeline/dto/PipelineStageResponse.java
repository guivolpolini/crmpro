package com.crmpro.pipeline.dto;

import com.crmpro.pipeline.entity.PipelineStage;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PipelineStageResponse {
    private UUID id;
    private UUID organizationId;
    private String name;
    private Integer stageOrder;
    private String color;
    private boolean won;
    private boolean lost;

    public static PipelineStageResponse fromEntity(PipelineStage stage) {
        if (stage == null) return null;
        return PipelineStageResponse.builder()
                .id(stage.getId())
                .organizationId(stage.getOrganizationId())
                .name(stage.getName())
                .stageOrder(stage.getStageOrder())
                .color(stage.getColor())
                .won(stage.isWon())
                .lost(stage.isLost())
                .build();
    }
}
