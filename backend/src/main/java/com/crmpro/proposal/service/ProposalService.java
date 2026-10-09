package com.crmpro.proposal.service;

import com.crmpro.common.context.TenantContext;
import com.crmpro.common.exception.BusinessException;
import com.crmpro.common.exception.ResourceNotFoundException;
import com.crmpro.deal.repository.DealRepository;
import com.crmpro.proposal.dto.ProductRequest;
import com.crmpro.proposal.dto.ProductResponse;
import com.crmpro.proposal.dto.ProposalRequest;
import com.crmpro.proposal.dto.ProposalResponse;
import com.crmpro.proposal.entity.Product;
import com.crmpro.proposal.entity.Proposal;
import com.crmpro.proposal.repository.ProductRepository;
import com.crmpro.proposal.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProductRepository productRepository;
    private final ProposalRepository proposalRepository;
    private final DealRepository dealRepository;

    // --- Products ---

    @Transactional(readOnly = true)
    public List<ProductResponse> findAllProducts() {
        UUID tenantId = TenantContext.getTenantId();
        return productRepository.findAllByOrganizationIdAndActiveTrue(tenantId).stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .unitPrice(request.getUnitPrice() != null ? request.getUnitPrice() : BigDecimal.ZERO)
                .unit(request.getUnit() != null ? request.getUnit() : "UN")
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        return ProductResponse.fromEntity(productRepository.save(product));
    }

    // --- Proposals ---

    @Transactional(readOnly = true)
    public List<ProposalResponse> findAllProposals() {
        UUID tenantId = TenantContext.getTenantId();
        return proposalRepository.findAllByOrganizationIdOrderByCreatedAtDesc(tenantId).stream()
                .map(ProposalResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProposalResponse> findProposalsByDeal(UUID dealId) {
        UUID tenantId = TenantContext.getTenantId();
        return proposalRepository.findAllByOrganizationIdAndDealId(tenantId, dealId).stream()
                .map(ProposalResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ProposalResponse createProposal(ProposalRequest request) {
        UUID tenantId = TenantContext.getTenantId();

        dealRepository.findByIdAndOrganizationId(request.getDealId(), tenantId)
                .orElseThrow(() -> new BusinessException("Negócio associado não encontrado na organização"));

        String code = request.getCode();
        if (code == null || code.isBlank()) {
            code = "PROP-" + LocalDate.now().getYear() + "-" + (System.currentTimeMillis() % 10000);
        }

        Proposal proposal = Proposal.builder()
                .dealId(request.getDealId())
                .code(code)
                .totalAmount(request.getTotalAmount())
                .discount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO)
                .status(request.getStatus() != null ? request.getStatus() : "DRAFT")
                .validUntil(request.getValidUntil())
                .notes(request.getNotes())
                .build();

        return ProposalResponse.fromEntity(proposalRepository.save(proposal));
    }

    @Transactional
    public ProposalResponse updateStatus(UUID id, String status) {
        UUID tenantId = TenantContext.getTenantId();
        Proposal proposal = proposalRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Proposta não encontrada com o ID: " + id));

        proposal.setStatus(status);
        return ProposalResponse.fromEntity(proposalRepository.save(proposal));
    }
}
