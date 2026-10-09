package com.crmpro.report.service;

import com.crmpro.common.context.TenantContext;
import com.crmpro.deal.entity.Deal;
import com.crmpro.deal.entity.DealStatus;
import com.crmpro.deal.repository.DealRepository;
import com.crmpro.lead.repository.LeadRepository;
import com.crmpro.report.dto.PerformanceReportResponse;
import com.crmpro.user.entity.User;
import com.crmpro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final DealRepository dealRepository;
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PerformanceReportResponse getPerformanceReport() {
        UUID organizationId = TenantContext.getTenantId();

        List<Deal> deals = dealRepository.findAllByOrganizationId(organizationId);
        long totalLeads = leadRepository.countByOrganizationId(organizationId);
        long convertedLeads = leadRepository.countByOrganizationIdAndStatus(organizationId, com.crmpro.lead.entity.LeadStatus.CONVERTED);

        List<Deal> wonDeals = deals.stream()
                .filter(d -> d.getStatus() == DealStatus.WON)
                .toList();

        List<Deal> lostDeals = deals.stream()
                .filter(d -> d.getStatus() == DealStatus.LOST)
                .toList();

        List<Deal> openDeals = deals.stream()
                .filter(d -> d.getStatus() == DealStatus.OPEN)
                .toList();

        BigDecimal totalRevenueWon = wonDeals.stream()
                .map(d -> d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgTicket = wonDeals.isEmpty()
                ? BigDecimal.ZERO
                : totalRevenueWon.divide(BigDecimal.valueOf(wonDeals.size()), 2, RoundingMode.HALF_UP);

        long closedCount = wonDeals.size() + lostDeals.size();
        double winRate = closedCount == 0
                ? 0.0
                : ((double) wonDeals.size() / closedCount) * 100.0;

        double leadConversionRate = totalLeads == 0
                ? 0.0
                : ((double) convertedLeads / totalLeads) * 100.0;

        // Grouping monthly trends
        Map<String, List<Deal>> monthlyMap = deals.stream()
                .collect(Collectors.groupingBy(d -> {
                    if (d.getCreatedAt() == null) return "Atual";
                    ZonedDateTime zdt = d.getCreatedAt().atZone(ZoneId.of("America/Sao_Paulo"));
                    return zdt.getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"))
                            + "/" + zdt.getYear();
                }));

        List<PerformanceReportResponse.MonthlyTrend> monthlyTrends = monthlyMap.entrySet().stream()
                .map(entry -> {
                    BigDecimal rev = entry.getValue().stream()
                            .filter(d -> d.getStatus() == DealStatus.WON)
                            .map(d -> d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return PerformanceReportResponse.MonthlyTrend.builder()
                            .month(entry.getKey())
                            .revenue(rev)
                            .dealsCount(entry.getValue().size())
                            .build();
                })
                .toList();

        // Top sellers by won deals
        Map<UUID, List<Deal>> dealsByOwner = wonDeals.stream()
                .filter(d -> d.getAssignedToId() != null)
                .collect(Collectors.groupingBy(Deal::getAssignedToId));

        List<PerformanceReportResponse.TopSeller> topSellers = dealsByOwner.entrySet().stream()
                .map(entry -> {
                    Optional<User> ownerOpt = userRepository.findById(entry.getKey());
                    String name = ownerOpt.map(User::getName).orElse("Representante Comercial");
                    String email = ownerOpt.map(User::getEmail).orElse("-");
                    BigDecimal rev = entry.getValue().stream()
                            .map(d -> d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return PerformanceReportResponse.TopSeller.builder()
                            .sellerName(name)
                            .sellerEmail(email)
                            .wonDealsCount(entry.getValue().size())
                            .totalRevenue(rev)
                            .build();
                })
                .sorted(Comparator.comparing(PerformanceReportResponse.TopSeller::getTotalRevenue).reversed())
                .limit(5)
                .toList();

        return PerformanceReportResponse.builder()
                .totalRevenueWon(totalRevenueWon)
                .averageTicket(avgTicket)
                .totalWonDeals(wonDeals.size())
                .totalLostDeals(lostDeals.size())
                .totalOpenDeals(openDeals.size())
                .winRate(Math.round(winRate * 10.0) / 10.0)
                .leadConversionRate(Math.round(leadConversionRate * 10.0) / 10.0)
                .monthlyTrends(monthlyTrends)
                .topSellers(topSellers)
                .build();
    }

    @Transactional(readOnly = true)
    public String exportDealsToCsv() {
        UUID organizationId = TenantContext.getTenantId();
        List<Deal> deals = dealRepository.findAllByOrganizationId(organizationId);

        StringBuilder sb = new StringBuilder();
        sb.append("ID,Titulo,Valor,Status,Data_Criacao\n");

        for (Deal deal : deals) {
            sb.append(deal.getId()).append(",")
              .append("\"").append(deal.getTitle().replace("\"", "\"\"")).append("\",")
              .append(deal.getAmount() != null ? deal.getAmount() : BigDecimal.ZERO).append(",")
              .append(deal.getStatus()).append(",")
              .append(deal.getCreatedAt() != null ? deal.getCreatedAt() : "").append("\n");
        }

        return sb.toString();
    }
}
