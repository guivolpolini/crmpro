export interface PipelineStage {
  id: string
  organizationId: string
  name: string
  stageOrder: number
  color: string
  won: boolean
  lost: boolean
}

export type DealStatus = 'OPEN' | 'WON' | 'LOST'

export interface Deal {
  id: string
  organizationId: string
  stageId: string
  stageName?: string
  stageColor?: string
  companyId?: string
  companyName?: string
  contactId?: string
  contactName?: string
  assignedToId?: string
  assignedToName?: string
  title: string
  amount: number
  probability: number
  expectedCloseDate?: string
  closedAt?: string
  lostReason?: string
  status: DealStatus
  createdAt: string
}
