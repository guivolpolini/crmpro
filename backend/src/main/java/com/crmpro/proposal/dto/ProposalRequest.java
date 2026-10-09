package com.crmpro.proposal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ProposalRequest {

    @NotNull(message = "O negócio vinculado é obrigatório")
    private UUID dealId;

    private String code;

    @NotNull(message = "O valor total é obrigatório")
    private BigDecimal totalAmount;

    private BigDecimal discount;
    private String status; // DRAFT, SENT, ACCEPTED, REJECTED
    private LocalDate validUntil;
    private String notes;
}
