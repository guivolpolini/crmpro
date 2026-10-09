import React from 'react'
import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '@/features/auth/AuthContext'
import {
  Building2,
  LayoutDashboard,
  Kanban,
  Users,
  Briefcase,
  Contact2,
  CheckSquare,
  FileSpreadsheet,
  BarChart3,
  LogOut,
  ShieldCheck,
  ChevronRight
} from 'lucide-react'

export const AppLayout: React.FC = () => {
  const { user, organization, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'Funil Kanban', path: '/pipeline', icon: Kanban },
    { name: 'Leads', path: '/leads', icon: Users },
    { name: 'Negócios / Oportunidades', path: '/deals', icon: Briefcase },
    { name: 'Contatos', path: '/contacts', icon: Contact2 },
    { name: 'Empresas Clientes', path: '/companies', icon: Building2 },
    { name: 'Tarefas & Atividades', path: '/tasks', icon: CheckSquare },
    { name: 'Propostas Comerciais', path: '/proposals', icon: FileSpreadsheet },
    { name: 'Relatórios & Métricas', path: '/reports', icon: BarChart3 }
  ]

  return (
    <div className="flex h-screen bg-slate-50 overflow-hidden font-sans">
      {/* Sidebar */}
      <aside className="w-64 bg-slate-900 border-r border-slate-800 flex flex-col justify-between shrink-0">
        <div>
          {/* Brand header */}
          <div className="h-16 flex items-center gap-3 px-6 border-b border-slate-800/80">
            <div className="w-9 h-9 rounded-lg bg-blue-600 flex items-center justify-center text-white shadow-md shadow-blue-500/30">
              <Building2 className="w-5 h-5" />
            </div>
            <div>
              <span className="font-bold text-base text-white tracking-tight flex items-center gap-1.5">
                CRM PRO
                <span className="text-[10px] uppercase font-bold px-1.5 py-0.5 rounded bg-blue-500/20 text-blue-400">
                  SaaS
                </span>
              </span>
              <p className="text-xs text-slate-400 truncate max-w-[130px]">
                {organization?.name || 'Organização'}
              </p>
            </div>
          </div>

          {/* Navigation Links */}
          <nav className="p-3 space-y-1">
            {navItems.map((item) => {
              const Icon = item.icon
              return (
                <NavLink
                  key={item.path}
                  to={item.path}
                  className={({ isActive }) =>
                    `flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all ${
                      isActive
                        ? 'bg-blue-600 text-white shadow-sm shadow-blue-600/30'
                        : 'text-slate-400 hover:text-slate-100 hover:bg-slate-800/60'
                    }`
                  }
                >
                  <Icon className="w-4 h-4 shrink-0" />
                  <span className="truncate">{item.name}</span>
                </NavLink>
              )
            })}
          </nav>
        </div>

        {/* User profile & Logout footer */}
        <div className="p-4 border-t border-slate-800 bg-slate-950/40">
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center gap-2.5 overflow-hidden">
              <div className="w-8 h-8 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-xs font-semibold text-slate-200">
                {user?.name?.charAt(0).toUpperCase() || 'U'}
              </div>
              <div className="overflow-hidden">
                <p className="text-xs font-semibold text-white truncate">{user?.name}</p>
                <div className="flex items-center gap-1 text-[11px] text-emerald-400 font-medium">
                  <ShieldCheck className="w-3 h-3" />
                  <span>{user?.role}</span>
                </div>
              </div>
            </div>
            <button
              onClick={handleLogout}
              title="Encerrar sessão"
              className="p-1.5 text-slate-400 hover:text-red-400 hover:bg-slate-800 rounded-lg transition-colors cursor-pointer"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>
          <div className="text-[11px] text-slate-500 flex items-center justify-between">
            <span>Plano: {organization?.plan || 'PRO'}</span>
            <span className="text-emerald-500 font-medium">PostgreSQL Ativo</span>
          </div>
        </div>
      </aside>

      {/* Main content body */}
      <main className="flex-1 flex flex-col overflow-y-auto">
        <header className="h-16 bg-white border-b border-slate-200/80 px-8 flex items-center justify-between shrink-0 sticky top-0 z-10 shadow-xs">
          <div className="flex items-center gap-2 text-sm text-slate-500">
            <span>CRM PRO</span>
            <ChevronRight className="w-3.5 h-3.5" />
            <span className="font-semibold text-slate-800">Ambiente Comercial</span>
          </div>

          <div className="flex items-center gap-4">
            <div className="flex items-center gap-2 text-xs bg-slate-100 px-3 py-1.5 rounded-full text-slate-600 font-medium">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
              Tenant: <span className="font-semibold text-slate-900">{organization?.name}</span>
            </div>
          </div>
        </header>

        <div className="p-8 flex-1">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
