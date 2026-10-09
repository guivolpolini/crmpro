package com.crmpro.pipeline.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PipelineStageRequest {

    @NotBlank(message = "O nome do estágio é obrigatório")
    private String name;

    private Integer stageOrder;
    private String color;
    private boolean won;
    private boolean lost;
}
