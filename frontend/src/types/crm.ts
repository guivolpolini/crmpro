export interface Company {
  id: string
  organizationId: string
  name: string
  tradeName?: string
  document?: string
  segment?: string
  website?: string
  phone?: string
  email?: string
  address?: string
  city?: string
  state?: string
  status: string
  createdAt: string
}

export interface Contact {
  id: string
  organizationId: string
  companyId?: string
  companyName?: string
  name: string
  email?: string
  phone?: string
  jobTitle?: string
  notes?: string
  status: string
  createdAt: string
}

export type LeadStatus = 'NEW' | 'CONTACTED' | 'QUALIFIED' | 'UNQUALIFIED' | 'CONVERTED'

export interface Lead {
  id: string
  organizationId: string
  name: string
  companyName?: string
  email?: string
  phone?: string
  source: string
  status: LeadStatus
  score: number
  notes?: string
  assignedToId?: string
  assignedToName?: string
  convertedAt?: string
  convertedContactId?: string
  convertedCompanyId?: string
  createdAt: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}
