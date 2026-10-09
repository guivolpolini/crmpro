package com.crmpro.proposal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "O nome do produto/serviço é obrigatório")
    private String name;

    private String code;
    private String description;

    @NotNull(message = "O preço unitário é obrigatório")
    private BigDecimal unitPrice;

    private String unit;
    private Boolean active;
}
