package com.crmpro.deal.dto;

import com.crmpro.deal.entity.DealStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class DealRequest {

    @NotBlank(message = "O título da oportunidade é obrigatório")
    private String title;

    @NotNull(message = "O estágio do funil é obrigatório")
    private UUID stageId;

    private UUID companyId;
    private UUID contactId;
    private UUID assignedToId;
    private BigDecimal amount;
    private Integer probability;
    private LocalDate expectedCloseDate;
    private DealStatus status;
    private String lostReason;
}
