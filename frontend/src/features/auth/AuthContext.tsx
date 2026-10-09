import React, { createContext, useContext, useState, useEffect } from 'react'
import { User, Organization, AuthResponse } from '@/types/auth'
import { api } from '@/lib/api'

interface AuthContextType {
  user: User | null
  organization: Organization | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (data: AuthResponse) => void
  logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null)
  const [organization, setOrganization] = useState<Organization | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const storedUser = localStorage.getItem('crmpro_user')
    const storedOrg = localStorage.getItem('crmpro_org')
    const token = localStorage.getItem('crmpro_access_token')

    if (token && storedUser && storedOrg) {
      try {
        setUser(JSON.parse(storedUser))
        setOrganization(JSON.parse(storedOrg))
      } catch (e) {
        localStorage.clear()
      }
    }
    setIsLoading(false)
  }, [])

  const login = (data: AuthResponse) => {
    localStorage.setItem('crmpro_access_token', data.accessToken)
    localStorage.setItem('crmpro_refresh_token', data.refreshToken)
    localStorage.setItem('crmpro_user', JSON.stringify(data.user))
    localStorage.setItem('crmpro_org', JSON.stringify(data.organization))
    setUser(data.user)
    setOrganization(data.organization)
  }

  const logout = () => {
    localStorage.removeItem('crmpro_access_token')
    localStorage.removeItem('crmpro_refresh_token')
    localStorage.removeItem('crmpro_user')
    localStorage.removeItem('crmpro_org')
    setUser(null)
    setOrganization(null)
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        organization,
        isAuthenticated: !!user,
        isLoading,
        login,
        logout
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
