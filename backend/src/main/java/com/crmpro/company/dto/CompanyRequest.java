package com.crmpro.company.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyRequest {

    @NotBlank(message = "O nome da empresa é obrigatório")
    private String name;

    private String tradeName;
    private String document;
    private String segment;
    private String website;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String state;
    private String status;
}
