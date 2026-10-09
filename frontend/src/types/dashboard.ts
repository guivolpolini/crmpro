export interface StageMetric {
  stageName: string
  color: string
  dealsCount: number
  totalAmount: number
}

export interface DashboardStatsResponse {
  totalRevenueWon: number
  totalPipelineOpen: number
  totalDealsCount: number
  totalLeadsCount: number
  leadConversionRate: number
  totalCompaniesCount: number
  totalContactsCount: number
  pendingTasksCount: number
  stagesMetrics: StageMetric[]
}
