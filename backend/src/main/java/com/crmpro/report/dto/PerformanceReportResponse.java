package com.crmpro.report.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PerformanceReportResponse {

    private BigDecimal totalRevenueWon;
    private BigDecimal averageTicket;
    private long totalWonDeals;
    private long totalLostDeals;
    private long totalOpenDeals;
    private double winRate;
    private double leadConversionRate;

    private List<MonthlyTrend> monthlyTrends;
    private List<TopSeller> topSellers;

    @Data
    @Builder
    public static class MonthlyTrend {
        private String month;
        private BigDecimal revenue;
        private long dealsCount;
    }

    @Data
    @Builder
    public static class TopSeller {
        private String sellerName;
        private String sellerEmail;
        private long wonDealsCount;
        private BigDecimal totalRevenue;
    }
}
