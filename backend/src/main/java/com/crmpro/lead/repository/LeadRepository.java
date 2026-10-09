package com.crmpro.lead.repository;

import com.crmpro.lead.entity.Lead;
import com.crmpro.lead.entity.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID> {
    Page<Lead> findAllByOrganizationId(UUID organizationId, Pageable pageable);
    Optional<Lead> findByIdAndOrganizationId(UUID id, UUID organizationId);
    Page<Lead> findAllByOrganizationIdAndStatus(UUID organizationId, LeadStatus status, Pageable pageable);
    Page<Lead> findAllByOrganizationIdAndNameContainingIgnoreCase(UUID organizationId, String name, Pageable pageable);
    long countByOrganizationId(UUID organizationId);
    long countByOrganizationIdAndStatus(UUID organizationId, LeadStatus status);
}
