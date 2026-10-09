import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from '@/features/auth/AuthContext'
import { LoginPage } from '@/features/auth/pages/LoginPage'
import { RegisterPage } from '@/features/auth/pages/RegisterPage'
import { AppLayout } from '@/components/layout/AppLayout'
import { DashboardPage } from '@/features/dashboard/pages/DashboardPage'
import { LeadsPage } from '@/features/leads/pages/LeadsPage'
import { ContactsPage } from '@/features/contacts/pages/ContactsPage'
import { CompaniesPage } from '@/features/companies/pages/CompaniesPage'
import { KanbanBoardPage } from '@/features/pipeline/pages/KanbanBoardPage'
import { DealsListPage } from '@/features/deals/pages/DealsListPage'
import { TasksPage } from '@/features/tasks/pages/TasksPage'
import { ProposalsPage } from '@/features/proposals/pages/ProposalsPage'
import { ReportsPage } from '@/features/reports/pages/ReportsPage'
import { AiAssistantPage } from '@/features/ai/pages/AiAssistantPage'

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth()

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50">
        <div className="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
      </div>
    )
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
}

export function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route
            path="/"
            element={
              <ProtectedRoute>
                <AppLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<DashboardPage />} />
            <Route path="ai-assistant" element={<AiAssistantPage />} />
            <Route path="pipeline" element={<KanbanBoardPage />} />
            <Route path="deals" element={<DealsListPage />} />
            <Route path="leads" element={<LeadsPage />} />
            <Route path="contacts" element={<ContactsPage />} />
            <Route path="companies" element={<CompaniesPage />} />
            <Route path="tasks" element={<TasksPage />} />
            <Route path="proposals" element={<ProposalsPage />} />
            <Route path="reports" element={<ReportsPage />} />
          </Route>

          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App
