package com.crmpro.lead.dto;

import com.crmpro.lead.entity.LeadStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class LeadRequest {

    @NotBlank(message = "O nome do lead é obrigatório")
    private String name;

    private String companyName;
    private String email;
    private String phone;
    private String source;
    private LeadStatus status;
    private Integer score;
    private String notes;
    private UUID assignedToId;
}
