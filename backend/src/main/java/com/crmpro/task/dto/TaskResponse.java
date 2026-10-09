package com.crmpro.task.dto;

import com.crmpro.task.entity.Task;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class TaskResponse {
    private UUID id;
    private UUID organizationId;
    private UUID assignedToId;
    private String assignedToName;
    private UUID dealId;
    private String dealTitle;
    private UUID contactId;
    private String contactName;
    private String title;
    private String description;
    private Instant dueDate;
    private String priority;
    private String status;
    private Instant completedAt;
    private Instant createdAt;

    public static TaskResponse fromEntity(Task task) {
        if (task == null) return null;
        return TaskResponse.builder()
                .id(task.getId())
                .organizationId(task.getOrganizationId())
                .assignedToId(task.getAssignedToId())
                .assignedToName(task.getAssignedTo() != null ? task.getAssignedTo().getName() : null)
                .dealId(task.getDealId())
                .dealTitle(task.getDeal() != null ? task.getDeal().getTitle() : null)
                .contactId(task.getContactId())
                .contactName(task.getContact() != null ? task.getContact().getName() : null)
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .priority(task.getPriority())
                .status(task.getStatus())
                .completedAt(task.getCompletedAt())
                .createdAt(task.getCreatedAt())
                .build();
    }
}
