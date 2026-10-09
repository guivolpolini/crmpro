package com.crmpro.task.service;

import com.crmpro.common.context.TenantContext;
import com.crmpro.common.exception.ResourceNotFoundException;
import com.crmpro.task.dto.TaskRequest;
import com.crmpro.task.dto.TaskResponse;
import com.crmpro.task.entity.Task;
import com.crmpro.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public List<TaskResponse> findAll() {
        UUID tenantId = TenantContext.getTenantId();
        return taskRepository.findAllByOrganizationIdOrderByDueDateAsc(tenantId).stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        Task task = taskRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com o ID: " + id));
        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .priority(request.getPriority() != null ? request.getPriority() : "MEDIUM")
                .status("PENDING")
                .assignedToId(request.getAssignedToId())
                .dealId(request.getDealId())
                .contactId(request.getContactId())
                .build();
        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse update(UUID id, TaskRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        Task task = taskRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com o ID: " + id));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        task.setAssignedToId(request.getAssignedToId());
        task.setDealId(request.getDealId());
        task.setContactId(request.getContactId());

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse toggleStatus(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        Task task = taskRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com o ID: " + id));

        if ("COMPLETED".equalsIgnoreCase(task.getStatus())) {
            task.setStatus("PENDING");
            task.setCompletedAt(null);
        } else {
            task.setStatus("COMPLETED");
            task.setCompletedAt(Instant.now());
        }

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional
    public void delete(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        Task task = taskRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com o ID: " + id));
        taskRepository.delete(task);
    }
}
