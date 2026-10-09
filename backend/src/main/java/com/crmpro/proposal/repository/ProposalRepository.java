package com.crmpro.proposal.repository;

import com.crmpro.proposal.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, UUID> {
    List<Proposal> findAllByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);
    List<Proposal> findAllByOrganizationIdAndDealId(UUID organizationId, UUID dealId);
    Optional<Proposal> findByIdAndOrganizationId(UUID id, UUID organizationId);
    long countByOrganizationId(UUID organizationId);
}
