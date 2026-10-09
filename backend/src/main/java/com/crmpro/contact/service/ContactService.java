package com.crmpro.contact.service;

import com.crmpro.common.context.TenantContext;
import com.crmpro.common.exception.BusinessException;
import com.crmpro.common.exception.ResourceNotFoundException;
import com.crmpro.company.repository.CompanyRepository;
import com.crmpro.contact.dto.ContactRequest;
import com.crmpro.contact.dto.ContactResponse;
import com.crmpro.contact.entity.Contact;
import com.crmpro.contact.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public Page<ContactResponse> findAll(String search, Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        Page<Contact> page = (search != null && !search.isBlank())
                ? contactRepository.findAllByOrganizationIdAndNameContainingIgnoreCase(tenantId, search, pageable)
                : contactRepository.findAllByOrganizationId(tenantId, pageable);
        return page.map(ContactResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public ContactResponse findById(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        Contact contact = contactRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado com o ID: " + id));
        return ContactResponse.fromEntity(contact);
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> findByCompany(UUID companyId) {
        UUID tenantId = TenantContext.getTenantId();
        return contactRepository.findAllByOrganizationIdAndCompanyId(tenantId, companyId).stream()
                .map(ContactResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ContactResponse create(ContactRequest request) {
        UUID tenantId = TenantContext.getTenantId();

        if (request.getCompanyId() != null) {
            boolean companyExists = companyRepository.findByIdAndOrganizationId(request.getCompanyId(), tenantId).isPresent();
            if (!companyExists) {
                throw new BusinessException("A empresa vinculada não pertence a sua organização ou não existe");
            }
        }

        Contact contact = Contact.builder()
                .name(request.getName())
                .companyId(request.getCompanyId())
                .email(request.getEmail())
                .phone(request.getPhone())
                .jobTitle(request.getJobTitle())
                .notes(request.getNotes())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .build();

        return ContactResponse.fromEntity(contactRepository.save(contact));
    }

    @Transactional
    public ContactResponse update(UUID id, ContactRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        Contact contact = contactRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado com o ID: " + id));

        if (request.getCompanyId() != null) {
            boolean companyExists = companyRepository.findByIdAndOrganizationId(request.getCompanyId(), tenantId).isPresent();
            if (!companyExists) {
                throw new BusinessException("A empresa vinculada não pertence a sua organização ou não existe");
            }
        }

        contact.setName(request.getName());
        contact.setCompanyId(request.getCompanyId());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setJobTitle(request.getJobTitle());
        contact.setNotes(request.getNotes());
        if (request.getStatus() != null) {
            contact.setStatus(request.getStatus());
        }

        return ContactResponse.fromEntity(contactRepository.save(contact));
    }

    @Transactional
    public void delete(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        Contact contact = contactRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado com o ID: " + id));
        contactRepository.delete(contact);
    }
}
