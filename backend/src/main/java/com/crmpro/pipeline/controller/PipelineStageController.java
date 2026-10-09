package com.crmpro.pipeline.controller;

import com.crmpro.common.response.ApiResponse;
import com.crmpro.pipeline.dto.PipelineStageRequest;
import com.crmpro.pipeline.dto.PipelineStageResponse;
import com.crmpro.pipeline.service.PipelineStageService;
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
@RequestMapping("/api/v1/pipeline-stages")
@RequiredArgsConstructor
@Tag(name = "Estágios do Funil", description = "Endpoints para gerenciamento dos estágios do pipeline de vendas")
public class PipelineStageController {

    private final PipelineStageService stageService;

    @GetMapping
    @Operation(summary = "Listar estágios do pipeline da organização autenticada")
    public ResponseEntity<ApiResponse<List<PipelineStageResponse>>> findAll() {
        List<PipelineStageResponse> stages = stageService.getStagesForCurrentTenant();
        return ResponseEntity.ok(ApiResponse.ok(stages));
    }

    @PostMapping
    @Operation(summary = "Criar novo estágio personalizado")
    public ResponseEntity<ApiResponse<PipelineStageResponse>> create(@Valid @RequestBody PipelineStageRequest request) {
        PipelineStageResponse response = stageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Estágio criado com sucesso", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar estágio existente")
    public ResponseEntity<ApiResponse<PipelineStageResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody PipelineStageRequest request
    ) {
        PipelineStageResponse response = stageService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Estágio atualizado com sucesso", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover estágio")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        stageService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Estágio removido com sucesso", null));
    }
}
