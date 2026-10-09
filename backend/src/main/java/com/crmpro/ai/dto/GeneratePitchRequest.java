package com.crmpro.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GeneratePitchRequest {

    @NotBlank(message = "O nome do lead ou empresa é obrigatório")
    private String recipientName;

    private String segment;

    private String painPoints;

    private String targetProductName;
}
