package com.crmpro.dashboard.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardStatsResponse {

    private BigDecimal totalRevenueWon;
    private BigDecimal totalPipelineOpen;
    private long totalDealsCount;
    private long totalLeadsCount;
    private double leadConversionRate;
    private long totalCompaniesCount;
    private long totalContactsCount;
    private long pendingTasksCount;

    private List<StageMetric> stagesMetrics;

    @Data
    @Builder
    public static class StageMetric {
        private String stageName;
        private String color;
        private long dealsCount;
        private BigDecimal totalAmount;
    }
}
