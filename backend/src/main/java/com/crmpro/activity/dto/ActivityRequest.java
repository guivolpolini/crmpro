package com.crmpro.activity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ActivityRequest {

    @NotBlank(message = "O título da atividade é obrigatório")
    private String title;

    @NotBlank(message = "O tipo da atividade é obrigatório")
    private String type; // CALL, EMAIL, MEETING, NOTE

    private String description;
    private Instant activityDate;
    private UUID dealId;
    private UUID contactId;
}
