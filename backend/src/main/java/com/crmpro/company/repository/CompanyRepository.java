package com.crmpro.company.repository;

import com.crmpro.company.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Page<Company> findAllByOrganizationId(UUID organizationId, Pageable pageable);
    Optional<Company> findByIdAndOrganizationId(UUID id, UUID organizationId);
    Page<Company> findAllByOrganizationIdAndNameContainingIgnoreCase(UUID organizationId, String name, Pageable pageable);
    long countByOrganizationId(UUID organizationId);
}
