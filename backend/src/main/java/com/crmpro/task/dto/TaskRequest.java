package com.crmpro.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TaskRequest {

    @NotBlank(message = "O título da tarefa é obrigatório")
    private String title;

    private String description;
    private Instant dueDate;
    private String priority;
    private String status;
    private UUID assignedToId;
    private UUID dealId;
    private UUID contactId;
}
