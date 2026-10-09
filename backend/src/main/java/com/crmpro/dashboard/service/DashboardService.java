package com.crmpro.dashboard.service;

import com.crmpro.common.context.TenantContext;
import com.crmpro.company.repository.CompanyRepository;
import com.crmpro.contact.repository.ContactRepository;
import com.crmpro.dashboard.dto.DashboardStatsResponse;
import com.crmpro.deal.entity.Deal;
import com.crmpro.deal.entity.DealStatus;
import com.crmpro.deal.repository.DealRepository;
import com.crmpro.lead.entity.LeadStatus;
import com.crmpro.lead.repository.LeadRepository;
import com.crmpro.pipeline.entity.PipelineStage;
import com.crmpro.pipeline.repository.PipelineStageRepository;
import com.crmpro.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DealRepository dealRepository;
    private final LeadRepository leadRepository;
    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final TaskRepository taskRepository;
    private final PipelineStageRepository stageRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        UUID tenantId = TenantContext.getTenantId();

        BigDecimal wonRevenue = dealRepository.sumAmountByOrganizationIdAndStatus(tenantId, DealStatus.WON);
        BigDecimal openPipeline = dealRepository.sumAmountByOrganizationIdAndStatus(tenantId, DealStatus.OPEN);

        long totalDeals = dealRepository.countByOrganizationId(tenantId);
        long totalLeads = leadRepository.countByOrganizationId(tenantId);
        long convertedLeads = leadRepository.countByOrganizationIdAndStatus(tenantId, LeadStatus.CONVERTED);

        double conversionRate = totalLeads > 0
                ? ((double) convertedLeads / totalLeads) * 100.0
                : 0.0;

        long totalCompanies = companyRepository.countByOrganizationId(tenantId);
        long totalContacts = contactRepository.countByOrganizationId(tenantId);
        long pendingTasks = taskRepository.countByOrganizationIdAndStatus(tenantId, "PENDING");

        // Métricas por estágio do funil
        List<PipelineStage> stages = stageRepository.findAllByOrganizationIdOrderByStageOrderAsc(tenantId);
        List<Deal> deals = dealRepository.findAllByOrganizationId(tenantId);

        Map<UUID, List<Deal>> dealsByStage = deals.stream()
                .collect(Collectors.groupingBy(Deal::getStageId));

        List<DashboardStatsResponse.StageMetric> stageMetrics = new ArrayList<>();
        for (PipelineStage stage : stages) {
            List<Deal> stageDeals = dealsByStage.getOrDefault(stage.getId(), List.of());
            BigDecimal stageSum = stageDeals.stream()
                    .map(Deal::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            stageMetrics.add(DashboardStatsResponse.StageMetric.builder()
                    .stageName(stage.getName())
                    .color(stage.getColor())
                    .dealsCount(stageDeals.size())
                    .totalAmount(stageSum)
                    .build());
        }

        return DashboardStatsResponse.builder()
                .totalRevenueWon(wonRevenue != null ? wonRevenue : BigDecimal.ZERO)
                .totalPipelineOpen(openPipeline != null ? openPipeline : BigDecimal.ZERO)
                .totalDealsCount(totalDeals)
                .totalLeadsCount(totalLeads)
                .leadConversionRate(Math.round(conversionRate * 10.0) / 10.0)
                .totalCompaniesCount(totalCompanies)
                .totalContactsCount(totalContacts)
                .pendingTasksCount(pendingTasks)
                .stagesMetrics(stageMetrics)
                .build();
    }
}
