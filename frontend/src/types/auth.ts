export interface User {
  id: string
  organizationId: string
  name: string
  email: string
  role: 'ADMIN' | 'MANAGER' | 'SELLER' | 'VIEWER'
  avatarUrl?: string
  status: string
  createdAt: string
}

export interface Organization {
  id: string
  name: string
  legalName?: string
  document?: string
  email?: string
  phone?: string
  address?: string
  logoUrl?: string
  plan: string
  status: string
  createdAt: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  user: User
  organization: Organization
}

export interface ApiResponse<T> {
  success: boolean
  message?: string
  data: T
  errors?: string[]
  timestamp: string
}
