package com.crmpro.deal.service;

import com.crmpro.common.context.TenantContext;
import com.crmpro.common.exception.BusinessException;
import com.crmpro.common.exception.ResourceNotFoundException;
import com.crmpro.company.repository.CompanyRepository;
import com.crmpro.contact.repository.ContactRepository;
import com.crmpro.deal.dto.DealRequest;
import com.crmpro.deal.dto.DealResponse;
import com.crmpro.deal.dto.MoveStageRequest;
import com.crmpro.deal.entity.Deal;
import com.crmpro.deal.entity.DealStatus;
import com.crmpro.deal.repository.DealRepository;
import com.crmpro.pipeline.entity.PipelineStage;
import com.crmpro.pipeline.repository.PipelineStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;
    private final PipelineStageRepository stageRepository;
    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;

    @Transactional(readOnly = true)
    public List<DealResponse> findAllForKanban() {
        UUID tenantId = TenantContext.getTenantId();
        return dealRepository.findAllByOrganizationId(tenantId).stream()
                .map(DealResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<DealResponse> findAll(Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        return dealRepository.findAllByOrganizationId(tenantId, pageable)
                .map(DealResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public DealResponse findById(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        Deal deal = dealRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Negócio não encontrado com o ID: " + id));
        return DealResponse.fromEntity(deal);
    }

    @Transactional
    public DealResponse create(DealRequest request) {
        UUID tenantId = TenantContext.getTenantId();

        PipelineStage stage = stageRepository.findByIdAndOrganizationId(request.getStageId(), tenantId)
                .orElseThrow(() -> new BusinessException("Estágio inválido ou não pertencente a sua organização"));

        if (request.getCompanyId() != null) {
            companyRepository.findByIdAndOrganizationId(request.getCompanyId(), tenantId)
                    .orElseThrow(() -> new BusinessException("Empresa associada não pertence a sua organização"));
        }

        if (request.getContactId() != null) {
            contactRepository.findByIdAndOrganizationId(request.getContactId(), tenantId)
                    .orElseThrow(() -> new BusinessException("Contato associado não pertence a sua organização"));
        }

        Deal deal = Deal.builder()
                .title(request.getTitle())
                .stageId(stage.getId())
                .companyId(request.getCompanyId())
                .contactId(request.getContactId())
                .assignedToId(request.getAssignedToId())
                .amount(request.getAmount() != null ? request.getAmount() : BigDecimal.ZERO)
                .probability(request.getProbability() != null ? request.getProbability() : 50)
                .expectedCloseDate(request.getExpectedCloseDate())
                .status(stage.isWon() ? DealStatus.WON : (stage.isLost() ? DealStatus.LOST : DealStatus.OPEN))
                .lostReason(request.getLostReason())
                .build();

        return DealResponse.fromEntity(dealRepository.save(deal));
    }

    @Transactional
    public DealResponse update(UUID id, DealRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        Deal deal = dealRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Negócio não encontrado com o ID: " + id));

        PipelineStage stage = stageRepository.findByIdAndOrganizationId(request.getStageId(), tenantId)
                .orElseThrow(() -> new BusinessException("Estágio inválido"));

        deal.setTitle(request.getTitle());
        deal.setStageId(stage.getId());
        deal.setCompanyId(request.getCompanyId());
        deal.setContactId(request.getContactId());
        deal.setAssignedToId(request.getAssignedToId());
        if (request.getAmount() != null) deal.setAmount(request.getAmount());
        if (request.getProbability() != null) deal.setProbability(request.getProbability());
        deal.setExpectedCloseDate(request.getExpectedCloseDate());
        if (request.getStatus() != null) deal.setStatus(request.getStatus());
        deal.setLostReason(request.getLostReason());

        return DealResponse.fromEntity(dealRepository.save(deal));
    }

    @Transactional
    public DealResponse moveStage(UUID dealId, MoveStageRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        Deal deal = dealRepository.findByIdAndOrganizationId(dealId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Negócio não encontrado com o ID: " + dealId));

        PipelineStage targetStage = stageRepository.findByIdAndOrganizationId(request.getTargetStageId(), tenantId)
                .orElseThrow(() -> new BusinessException("Estágio de destino inválido"));

        deal.setStageId(targetStage.getId());

        if (targetStage.isWon()) {
            deal.setStatus(DealStatus.WON);
            deal.setClosedAt(Instant.now());
            deal.setProbability(100);
        } else if (targetStage.isLost()) {
            deal.setStatus(DealStatus.LOST);
            deal.setClosedAt(Instant.now());
            deal.setProbability(0);
            deal.setLostReason(request.getLostReason() != null ? request.getLostReason() : "Não especificado");
        } else {
            deal.setStatus(DealStatus.OPEN);
            deal.setClosedAt(null);
            deal.setLostReason(null);
        }

        return DealResponse.fromEntity(dealRepository.save(deal));
    }

    @Transactional
    public void delete(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        Deal deal = dealRepository.findByIdAndOrganizationId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Negócio não encontrado com o ID: " + id));
        dealRepository.delete(deal);
    }
}
