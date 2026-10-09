package com.crmpro.pipeline.service;

import com.crmpro.common.context.TenantContext;
import com.crmpro.common.exception.ResourceNotFoundException;
import com.crmpro.pipeline.dto.PipelineStageRequest;
import com.crmpro.pipeline.dto.PipelineStageResponse;
import com.crmpro.pipeline.entity.PipelineStage;
import com.crmpro.pipeline.repository.PipelineStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PipelineStageService {

    private final PipelineStageRepository stageRepository;

    @Transactional
    public List<PipelineStageResponse> getStagesForCurrentTenant() {
        UUID tenantId = TenantContext.getTenantId();
        List<PipelineStage> stages = stageRepository.findAllByOrganizationIdOrderByStageOrderAsc(tenantId);
        if (stages.isEmpty()) {
            stages = initializeDefaultStages(tenantId);
        }
        return stages.stream().map(PipelineStageResponse::fromEntity).toList();
    }

    @Transactional
    public List<PipelineStage> initializeDefaultStages(UUID tenantId) {
        List<PipelineStage> defaults = List.of(
                PipelineStage.builder().name("Prospecção").stageOrder(1).color("#3b82f6").won(false).lost(false).build(),
                PipelineStage.builder().name("Qualificação").stageOrder(2).color("#8b5cf6").won(false).lost(false).build(),
                PipelineStage.builder().name("Apresentação / Proposta").stageOrder(3).color("#ec4899").won(false).lost(false).build(),
                PipelineStage.builder().name("Negociação").stageOrder(4).color("#f59e0b").won(false).lost(false).build(),
                PipelineStage.builder().name("Fechado Ganho").stageOrder(5).color("#10b981").won(true).lost(false).build(),
                PipelineStage.builder().name("Fechado Perdido").stageOrder(6).color("#ef4444").won(false).lost(true).build()
        );
        for (PipelineStage stage : defaults) {
            stage.setOrganizationId(tenantId);
        }
        return stageRepository.saveAll(defaults);
    }

    @Transactional
    public PipelineStageResponse create(PipelineStageRequest request) {
        PipelineStage stage = PipelineStage.builder()
                .name(request.getName())
                .stageOrder(request.getStageOrder() != null ? request.getStageOrder() : 0)
                .color(request.getColor() != null ? request.getColor() : "#3b82f6")
                .won(request.isWon())
                .lost(request.isLost())
                .build();
        return PipelineStageResponse.fromEntity(stageRepository.save(stage));
    }

    @Transactional
    public PipelineStageResponse update(UUID id, PipelineStageRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        PipelineStage stage = stageRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Estágio não encontrado com o ID: " + id));

        stage.setName(request.getName());
        if (request.getStageOrder() != null) stage.setStageOrder(request.getStageOrder());
        if (request.getColor() != null) stage.setColor(request.getColor());
        stage.setWon(request.isWon());
        stage.setLost(request.isLost());

        return PipelineStageResponse.fromEntity(stageRepository.save(stage));
    }

    @Transactional
    public void delete(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        PipelineStage stage = stageRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Estágio não encontrado com o ID: " + id));
        stageRepository.delete(stage);
    }
}
