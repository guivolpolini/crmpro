package com.crmpro.contact.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class ContactRequest {

    @NotBlank(message = "O nome do contato é obrigatório")
    private String name;

    private UUID companyId;
    private String email;
    private String phone;
    private String jobTitle;
    private String notes;
    private String status;
}
