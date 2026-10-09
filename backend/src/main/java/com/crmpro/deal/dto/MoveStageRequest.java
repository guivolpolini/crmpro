package com.crmpro.deal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class MoveStageRequest {

    @NotNull(message = "O novo estágio é obrigatório")
    private UUID targetStageId;

    private String lostReason;
}
