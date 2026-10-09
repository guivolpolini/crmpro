export interface MonthlyTrend {
  month: string
  revenue: number
  dealsCount: number
}

export interface TopSeller {
  sellerName: string
  sellerEmail: string
  wonDealsCount: number
  totalRevenue: number
}

export interface PerformanceReportResponse {
  totalRevenueWon: number
  averageTicket: number
  totalWonDeals: number
  totalLostDeals: number
  totalOpenDeals: number
  winRate: number
  leadConversionRate: number
  monthlyTrends: MonthlyTrend[]
  topSellers: TopSeller[]
}
