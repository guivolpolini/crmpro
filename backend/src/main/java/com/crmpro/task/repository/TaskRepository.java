package com.crmpro.task.repository;

import com.crmpro.task.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findAllByOrganizationIdOrderByDueDateAsc(UUID organizationId);
    Page<Task> findAllByOrganizationId(UUID organizationId, Pageable pageable);
    Optional<Task> findByIdAndOrganizationId(UUID id, UUID organizationId);
    List<Task> findAllByOrganizationIdAndDealId(UUID organizationId, UUID dealId);
    long countByOrganizationIdAndStatus(UUID organizationId, String status);
}
