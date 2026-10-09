package com.crmpro.pipeline.repository;

import com.crmpro.pipeline.entity.PipelineStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PipelineStageRepository extends JpaRepository<PipelineStage, UUID> {
    List<PipelineStage> findAllByOrganizationIdOrderByStageOrderAsc(UUID organizationId);
    Optional<PipelineStage> findByIdAndOrganizationId(UUID id, UUID organizationId);
    long countByOrganizationId(UUID organizationId);
}
