package com.crmpro.contact.repository;

import com.crmpro.contact.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    Page<Contact> findAllByOrganizationId(UUID organizationId, Pageable pageable);
    Optional<Contact> findByIdAndOrganizationId(UUID id, UUID organizationId);
    Page<Contact> findAllByOrganizationIdAndNameContainingIgnoreCase(UUID organizationId, String name, Pageable pageable);
    List<Contact> findAllByOrganizationIdAndCompanyId(UUID organizationId, UUID companyId);
    long countByOrganizationId(UUID organizationId);
}
