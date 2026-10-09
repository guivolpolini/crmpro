import React from 'react'
import { useAuth } from '@/features/auth/AuthContext'
import {
  TrendingUp,
  Users,
  Briefcase,
  CheckCircle2,
  DollarSign,
  ArrowUpRight,
  ShieldCheck,
  Building
} from 'lucide-react'

export const DashboardPage: React.FC = () => {
  const { user, organization } = useAuth()

  const stats = [
    { title: 'Negócios em Aberto', value: 'R$ 145.200', change: '+12.5%', icon: DollarSign, color: 'text-emerald-600 bg-emerald-50' },
    { title: 'Oportunidades Ativas', value: '24', change: '+4 nesta semana', icon: Briefcase, color: 'text-blue-600 bg-blue-50' },
    { title: 'Novos Leads (Mês)', value: '68', change: '+18.2%', icon: Users, color: 'text-purple-600 bg-purple-50' },
    { title: 'Taxa de Conversão', value: '28.4%', change: '+3.1%', icon: TrendingUp, color: 'text-amber-600 bg-amber-50' }
  ]

  return (
    <div className="space-y-8 max-w-7xl mx-auto">
      {/* Welcome banner */}
      <div className="bg-gradient-to-r from-blue-900 to-indigo-900 rounded-2xl p-6 text-white shadow-xl flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-blue-500/30 text-blue-200">
              Ambiente Autenticado
            </span>
            <span className="text-xs text-blue-300">|</span>
            <span className="text-xs text-blue-200 flex items-center gap-1">
              <Building className="w-3.5 h-3.5" />
              {organization?.name}
            </span>
          </div>
          <h2 className="text-2xl font-bold tracking-tight">
            Olá, {user?.name}! Bem-vindo ao CRM PRO.
          </h2>
          <p className="text-sm text-blue-200 mt-1 max-w-xl">
            Sistema comercial completo com isolamento multi-empresa, arquitetura Spring Boot 3.4 e interface de alta fidelidade.
          </p>
        </div>

        <div className="flex items-center gap-3 bg-white/10 backdrop-blur-xs p-3 rounded-xl border border-white/10 text-xs">
          <ShieldCheck className="w-5 h-5 text-emerald-400 shrink-0" />
          <div>
            <p className="font-semibold text-white">Multi-Tenancy Blindado</p>
            <p className="text-blue-200 text-[11px]">Tenant ID: {organization?.id?.substring(0, 8)}...</p>
          </div>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {stats.map((stat, i) => {
          const Icon = stat.icon
          return (
            <div key={i} className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-center justify-between">
              <div>
                <p className="text-xs font-semibold text-slate-500 uppercase">{stat.title}</p>
                <h3 className="text-2xl font-bold text-slate-900 mt-1">{stat.value}</h3>
                <p className="text-xs text-emerald-600 font-semibold mt-1 flex items-center gap-0.5">
                  <ArrowUpRight className="w-3.5 h-3.5" />
                  {stat.change}
                </p>
              </div>
              <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${stat.color}`}>
                <Icon className="w-6 h-6" />
              </div>
            </div>
          )
        })}
      </div>

      {/* Quick pipeline preview placeholder */}
      <div className="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h3 className="text-lg font-bold text-slate-900">Visão Geral do Funil de Vendas</h3>
            <p className="text-xs text-slate-500">Métricas e estágios em sincronização com o banco de dados</p>
          </div>
          <span className="text-xs px-3 py-1 bg-blue-50 text-blue-700 font-semibold rounded-full border border-blue-200">
            Fase 1: Fundação Concluída
          </span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pt-2">
          <div className="p-4 rounded-xl bg-slate-50 border border-slate-200/60">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-bold uppercase text-slate-600">Autenticação & Segurança</span>
              <CheckCircle2 className="w-4 h-4 text-emerald-500" />
            </div>
            <p className="text-xs text-slate-500">
              Tokens JWT com HMAC-SHA256, rotação de refresh tokens e isolamento no Spring Security.
            </p>
          </div>

          <div className="p-4 rounded-xl bg-slate-50 border border-slate-200/60">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-bold uppercase text-slate-600">Modelagem Relacional</span>
              <CheckCircle2 className="w-4 h-4 text-emerald-500" />
            </div>
            <p className="text-xs text-slate-500">
              PostgreSQL 16 com Flyway migrations versionadas e integridade referencial.
            </p>
          </div>

          <div className="p-4 rounded-xl bg-slate-50 border border-slate-200/60">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-bold uppercase text-slate-600">Qualidade & Testes</span>
              <CheckCircle2 className="w-4 h-4 text-emerald-500" />
            </div>
            <p className="text-xs text-slate-500">
              Testes automatizados MockMvc e isolamento multi-tenant aprovados com 100% de sucesso.
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
