package com.crmpro.company.service;

import com.crmpro.common.context.TenantContext;
import com.crmpro.common.exception.ResourceNotFoundException;
import com.crmpro.company.dto.CompanyRequest;
import com.crmpro.company.dto.CompanyResponse;
import com.crmpro.company.entity.Company;
import com.crmpro.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public Page<CompanyResponse> findAll(String search, Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        Page<Company> page = (search != null && !search.isBlank())
                ? companyRepository.findAllByOrganizationIdAndNameContainingIgnoreCase(tenantId, search, pageable)
                : companyRepository.findAllByOrganizationId(tenantId, pageable);
        return page.map(CompanyResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public CompanyResponse findById(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        Company company = companyRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));
        return CompanyResponse.fromEntity(company);
    }

    @Transactional
    public CompanyResponse create(CompanyRequest request) {
        Company company = Company.builder()
                .name(request.getName())
                .tradeName(request.getTradeName())
                .document(request.getDocument())
                .segment(request.getSegment())
                .website(request.getWebsite())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .build();
        return CompanyResponse.fromEntity(companyRepository.save(company));
    }

    @Transactional
    public CompanyResponse update(UUID id, CompanyRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        Company company = companyRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));

        company.setName(request.getName());
        company.setTradeName(request.getTradeName());
        company.setDocument(request.getDocument());
        company.setSegment(request.getSegment());
        company.setWebsite(request.getWebsite());
        company.setPhone(request.getPhone());
        company.setEmail(request.getEmail());
        company.setAddress(request.getAddress());
        company.setCity(request.getCity());
        company.setState(request.getState());
        if (request.getStatus() != null) {
            company.setStatus(request.getStatus());
        }

        return CompanyResponse.fromEntity(companyRepository.save(company));
    }

    @Transactional
    public void delete(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        Company company = companyRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));
        companyRepository.delete(company);
    }
}
