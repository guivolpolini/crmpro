export interface Task {
  id: string
  organizationId: string
  assignedToId?: string
  assignedToName?: string
  dealId?: string
  dealTitle?: string
  contactId?: string
  contactName?: string
  title: string
  description?: string
  dueDate?: string
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT'
  status: 'PENDING' | 'COMPLETED' | 'CANCELLED'
  completedAt?: string
  createdAt: string
}

export interface Activity {
  id: string
  organizationId: string
  dealId?: string
  dealTitle?: string
  contactId?: string
  contactName?: string
  userId?: string
  userName?: string
  type: string
  title: string
  description?: string
  activityDate: string
  createdAt: string
}

export interface Product {
  id: string
  organizationId: string
  name: string
  code?: string
  description?: string
  unitPrice: number
  unit: string
  active: boolean
  createdAt: string
}

export interface Proposal {
  id: string
  organizationId: string
  dealId: string
  dealTitle?: string
  companyName?: string
  code: string
  totalAmount: number
  discount: number
  status: 'DRAFT' | 'SENT' | 'ACCEPTED' | 'REJECTED'
  validUntil?: string
  notes?: string
  createdAt: string
}
