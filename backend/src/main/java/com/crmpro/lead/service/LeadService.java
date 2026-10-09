package com.crmpro.lead.service;

import com.crmpro.common.context.TenantContext;
import com.crmpro.common.exception.BusinessException;
import com.crmpro.common.exception.ResourceNotFoundException;
import com.crmpro.company.entity.Company;
import com.crmpro.company.repository.CompanyRepository;
import com.crmpro.contact.entity.Contact;
import com.crmpro.contact.repository.ContactRepository;
import com.crmpro.lead.dto.*;
import com.crmpro.lead.entity.Lead;
import com.crmpro.lead.entity.LeadStatus;
import com.crmpro.lead.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;

    @Transactional(readOnly = true)
    public Page<LeadResponse> findAll(String search, LeadStatus status, Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        if (status != null) {
            return leadRepository.findAllByOrganizationIdAndStatus(tenantId, status, pageable)
                    .map(LeadResponse::fromEntity);
        }
        if (search != null && !search.isBlank()) {
            return leadRepository.findAllByOrganizationIdAndNameContainingIgnoreCase(tenantId, search, pageable)
                    .map(LeadResponse::fromEntity);
        }
        return leadRepository.findAllByOrganizationId(tenantId, pageable)
                .map(LeadResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public LeadResponse findById(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        Lead lead = leadRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado com o ID: " + id));
        return LeadResponse.fromEntity(lead);
    }

    @Transactional
    public LeadResponse create(LeadRequest request) {
        Lead lead = Lead.builder()
                .name(request.getName())
                .companyName(request.getCompanyName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .source(request.getSource() != null ? request.getSource() : "WEBSITE")
                .status(request.getStatus() != null ? request.getStatus() : LeadStatus.NEW)
                .score(request.getScore() != null ? request.getScore() : 0)
                .notes(request.getNotes())
                .assignedToId(request.getAssignedToId())
                .build();

        return LeadResponse.fromEntity(leadRepository.save(lead));
    }

    @Transactional
    public LeadResponse update(UUID id, LeadRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        Lead lead = leadRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado com o ID: " + id));

        lead.setName(request.getName());
        lead.setCompanyName(request.getCompanyName());
        lead.setEmail(request.getEmail());
        lead.setPhone(request.getPhone());
        if (request.getSource() != null) lead.setSource(request.getSource());
        if (request.getStatus() != null) lead.setStatus(request.getStatus());
        if (request.getScore() != null) lead.setScore(request.getScore());
        lead.setNotes(request.getNotes());
        lead.setAssignedToId(request.getAssignedToId());

        return LeadResponse.fromEntity(leadRepository.save(lead));
    }

    @Transactional
    public void delete(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        Lead lead = leadRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado com o ID: " + id));
        leadRepository.delete(lead);
    }

    @Transactional
    public ConvertLeadResponse convertLead(UUID leadId, ConvertLeadRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        Lead lead = leadRepository.findByIdAndOrganizationId(leadId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado com o ID: " + leadId));

        if (lead.getStatus() == LeadStatus.CONVERTED) {
            throw new BusinessException("Este lead já foi convertido anteriormente");
        }

        // 1. Criar Empresa se solicitado ou se houver nome de empresa
        Company company = null;
        String finalCompanyName = (request.getCompanyName() != null && !request.getCompanyName().isBlank())
                ? request.getCompanyName()
                : lead.getCompanyName();

        if (request.isCreateCompany() && finalCompanyName != null && !finalCompanyName.isBlank()) {
            company = Company.builder()
                    .name(finalCompanyName)
                    .phone(lead.getPhone())
                    .email(lead.getEmail())
                    .status("ACTIVE")
                    .build();
            company = companyRepository.save(company);
        }

        // 2. Criar Contato vinculado
        Contact contact = Contact.builder()
                .name(lead.getName())
                .companyId(company != null ? company.getId() : null)
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .notes("Convertido a partir do lead ID: " + lead.getId() + "\nNotas originais: " + (lead.getNotes() != null ? lead.getNotes() : ""))
                .status("ACTIVE")
                .build();
        contact = contactRepository.save(contact);

        // 3. Atualizar Lead para CONVERTED
        lead.setStatus(LeadStatus.CONVERTED);
        lead.setConvertedAt(Instant.now());
        lead.setConvertedContactId(contact.getId());
        if (company != null) {
            lead.setConvertedCompanyId(company.getId());
        }
        leadRepository.save(lead);

        return ConvertLeadResponse.builder()
                .leadId(lead.getId())
                .contactId(contact.getId())
                .companyId(company != null ? company.getId() : null)
                .convertedAt(lead.getConvertedAt())
                .message("Lead convertido com sucesso em contato e empresa")
                .build();
    }
}
