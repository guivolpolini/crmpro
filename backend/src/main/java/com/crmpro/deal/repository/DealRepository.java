package com.crmpro.deal.repository;

import com.crmpro.deal.entity.Deal;
import com.crmpro.deal.entity.DealStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DealRepository extends JpaRepository<Deal, UUID> {
    List<Deal> findAllByOrganizationId(UUID organizationId);
    Page<Deal> findAllByOrganizationId(UUID organizationId, Pageable pageable);
    Optional<Deal> findByIdAndOrganizationId(UUID id, UUID organizationId);
    List<Deal> findAllByOrganizationIdAndStageId(UUID organizationId, UUID stageId);
    long countByOrganizationId(UUID organizationId);
    long countByOrganizationIdAndStatus(UUID organizationId, DealStatus status);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Deal d WHERE d.organizationId = :organizationId AND d.status = :status")
    BigDecimal sumAmountByOrganizationIdAndStatus(@Param("organizationId") UUID organizationId, @Param("status") DealStatus status);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Deal d WHERE d.organizationId = :organizationId")
    BigDecimal sumTotalAmountByOrganizationId(@Param("organizationId") UUID organizationId);
}
