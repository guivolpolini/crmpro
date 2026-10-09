package com.crmpro.activity.repository;

import com.crmpro.activity.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    List<Activity> findAllByOrganizationIdOrderByActivityDateDesc(UUID organizationId);
    List<Activity> findAllByOrganizationIdAndDealIdOrderByActivityDateDesc(UUID organizationId, UUID dealId);
    List<Activity> findAllByOrganizationIdAndContactIdOrderByActivityDateDesc(UUID organizationId, UUID contactId);
    Optional<Activity> findByIdAndOrganizationId(UUID id, UUID organizationId);
    long countByOrganizationId(UUID organizationId);
}
