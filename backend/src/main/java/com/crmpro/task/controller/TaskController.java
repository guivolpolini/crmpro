package com.crmpro.task.controller;

import com.crmpro.common.response.ApiResponse;
import com.crmpro.task.dto.TaskRequest;
import com.crmpro.task.dto.TaskResponse;
import com.crmpro.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tarefas", description = "Endpoints para gerenciamento de tarefas comerciais e prazos")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Listar tarefas ordenadas por vencimento")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> findAll() {
        List<TaskResponse> tasks = taskService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(tasks));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tarefa por ID")
    public ResponseEntity<ApiResponse<TaskResponse>> findById(@PathVariable UUID id) {
        TaskResponse response = taskService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping
    @Operation(summary = "Criar nova tarefa")
    public ResponseEntity<ApiResponse<TaskResponse>> create(@Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tarefa criada com sucesso", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tarefa existente")
    public ResponseEntity<ApiResponse<TaskResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody TaskRequest request
    ) {
        TaskResponse response = taskService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Tarefa atualizada com sucesso", response));
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Alternar status de conclusão da tarefa")
    public ResponseEntity<ApiResponse<TaskResponse>> toggleStatus(@PathVariable UUID id) {
        TaskResponse response = taskService.toggleStatus(id);
        return ResponseEntity.ok(ApiResponse.ok("Status da tarefa alterado", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover tarefa")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        taskService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Tarefa removida com sucesso", null));
    }
}
