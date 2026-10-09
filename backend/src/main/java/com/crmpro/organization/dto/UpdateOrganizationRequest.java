package com.crmpro.organization.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrganizationRequest {

    @NotBlank(message = "O nome da empresa é obrigatório")
    private String name;

    private String legalName;

    private String document;

    private String email;

    private String phone;

    private String address;

    private String logoUrl;
}
