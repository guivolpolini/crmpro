package com.crmpro.deal.controller;

import com.crmpro.common.response.ApiResponse;
import com.crmpro.deal.dto.DealRequest;
import com.crmpro.deal.dto.DealResponse;
import com.crmpro.deal.dto.MoveStageRequest;
import com.crmpro.deal.service.DealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deals")
@RequiredArgsConstructor
@Tag(name = "Negócios / Oportunidades", description = "Endpoints para gestão de oportunidades comerciais e movimentação no funil")
public class DealController {

    private final DealService dealService;

    @GetMapping("/kanban")
    @Operation(summary = "Listar todas as oportunidades abertas e fechadas agrupadas para o Kanban")
    public ResponseEntity<ApiResponse<List<DealResponse>>> findAllForKanban() {
        List<DealResponse> deals = dealService.findAllForKanban();
        return ResponseEntity.ok(ApiResponse.ok(deals));
    }

    @GetMapping
    @Operation(summary = "Listar oportunidades paginadas")
    public ResponseEntity<ApiResponse<Page<DealResponse>>> findAll(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        Page<DealResponse> page = dealService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar oportunidade por ID")
    public ResponseEntity<ApiResponse<DealResponse>> findById(@PathVariable UUID id) {
        DealResponse response = dealService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping
    @Operation(summary = "Criar nova oportunidade comercial")
    public ResponseEntity<ApiResponse<DealResponse>> create(@Valid @RequestBody DealRequest request) {
        DealResponse response = dealService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Oportunidade criada com sucesso", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar oportunidade existente")
    public ResponseEntity<ApiResponse<DealResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody DealRequest request
    ) {
        DealResponse response = dealService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Oportunidade atualizada com sucesso", response));
    }

    @PatchMapping("/{id}/move")
    @Operation(summary = "Mover oportunidade entre estágios do Kanban com validação")
    public ResponseEntity<ApiResponse<DealResponse>> moveStage(
            @PathVariable UUID id,
            @Valid @RequestBody MoveStageRequest request
    ) {
        DealResponse response = dealService.moveStage(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Estágio atualizado com sucesso", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover oportunidade")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        dealService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Oportunidade removida com sucesso", null));
    }
}
