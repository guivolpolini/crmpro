import React, { useEffect, useState } from 'react'
import { api } from '@/lib/api'
import { PerformanceReportResponse } from '@/types/report'
import {
  TrendingUp,
  Award,
  Download,
  DollarSign,
  ArrowUpRight,
  CheckCircle2,
  XCircle,
  Clock,
  RefreshCw,
  Users
} from 'lucide-react'
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
  Legend
} from 'recharts'

export const ReportsPage: React.FC = () => {
  const [report, setReport] = useState<PerformanceReportResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const [exporting, setExporting] = useState(false)

  const fetchReport = async () => {
    setLoading(true)
    try {
      const res = await api.get('/reports/performance')
      setReport(res.data.data)
    } catch (err) {
      console.error('Falha ao carregar relatório', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchReport()
  }, [])

  const handleExportCsv = async () => {
    setExporting(true)
    try {
      const response = await api.get('/reports/export/deals', {
        responseType: 'blob'
      })
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', `crmpro_deals_${new Date().toISOString().slice(0, 10)}.csv`)
      document.body.appendChild(link)
      link.click()
      link.remove()
    } catch (err) {
      console.error('Falha ao exportar CSV', err)
    } finally {
      setExporting(false)
    }
  }

  const formatCurrency = (val: number | undefined) => {
    if (val === undefined || val === null) return 'R$ 0,00'
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val)
  }

  return (
    <div className="space-y-8 max-w-7xl mx-auto pb-10">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-slate-900">
            Relatórios & Performance Comercial
          </h1>
          <p className="text-sm text-slate-500 mt-1">
            Análise consolidada de fechamentos, conversão de equipe e exportação de dados
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={fetchReport}
            disabled={loading}
            className="flex items-center gap-2 px-3.5 py-2 bg-white border border-slate-200 hover:bg-slate-50 rounded-xl text-xs font-semibold text-slate-700 shadow-xs"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
            Atualizar
          </button>

          <button
            onClick={handleExportCsv}
            disabled={exporting}
            className="flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 active:bg-emerald-800 text-white rounded-xl text-xs font-semibold shadow-xs transition-colors"
          >
            <Download className="w-3.5 h-3.5" />
            {exporting ? 'Exportando...' : 'Exportar CSV'}
          </button>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase text-slate-500">Receita Ganha</span>
            <div className="p-2 bg-emerald-50 text-emerald-600 rounded-lg">
              <DollarSign className="w-5 h-5" />
            </div>
          </div>
          <h3 className="text-2xl font-bold text-slate-900 mt-2">
            {loading ? '...' : formatCurrency(report?.totalRevenueWon)}
          </h3>
          <p className="text-xs text-slate-500 mt-1 flex items-center gap-1 font-medium">
            <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
            {report?.totalWonDeals ?? 0} negócios ganhos
          </p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase text-slate-500">Ticket Médio</span>
            <div className="p-2 bg-blue-50 text-blue-600 rounded-lg">
              <TrendingUp className="w-5 h-5" />
            </div>
          </div>
          <h3 className="text-2xl font-bold text-slate-900 mt-2">
            {loading ? '...' : formatCurrency(report?.averageTicket)}
          </h3>
          <p className="text-xs text-slate-500 mt-1 flex items-center gap-1 font-medium">
            <ArrowUpRight className="w-3.5 h-3.5 text-blue-600" />
            Por oportunidade ganha
          </p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase text-slate-500">Win Rate (Taxa de Ganho)</span>
            <div className="p-2 bg-purple-50 text-purple-600 rounded-lg">
              <Award className="w-5 h-5" />
            </div>
          </div>
          <h3 className="text-2xl font-bold text-slate-900 mt-2">
            {loading ? '...' : `${report?.winRate ?? 0}%`}
          </h3>
          <p className="text-xs text-slate-500 mt-1 flex items-center gap-1 font-medium">
            <span className="text-slate-700 font-semibold">{report?.totalLostDeals ?? 0}</span> perdidos vs{' '}
            <span className="text-emerald-600 font-semibold">{report?.totalWonDeals ?? 0}</span> ganhos
          </p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase text-slate-500">Pipeline Ativo</span>
            <div className="p-2 bg-amber-50 text-amber-600 rounded-lg">
              <Clock className="w-5 h-5" />
            </div>
          </div>
          <h3 className="text-2xl font-bold text-slate-900 mt-2">
            {loading ? '...' : `${report?.totalOpenDeals ?? 0} deals`}
          </h3>
          <p className="text-xs text-slate-500 mt-1 flex items-center gap-1 font-medium">
            Taxa de Leads: {report?.leadConversionRate ?? 0}%
          </p>
        </div>
      </div>

      {/* Grid: Gráfico de Tendência & Ranking de Vendedores */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Monthly Revenue Trend */}
        <div className="lg:col-span-2 bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs">
          <div className="mb-6">
            <h3 className="text-base font-bold text-slate-900">Evolução Mensal de Vendas</h3>
            <p className="text-xs text-slate-500">Receita fechada por período</p>
          </div>

          <div className="h-72 w-full">
            {!report?.monthlyTrends || report.monthlyTrends.length === 0 ? (
              <div className="h-full flex items-center justify-center text-slate-400 text-sm">
                Sem histórico temporal para exibição
              </div>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={report.monthlyTrends} margin={{ top: 10, right: 10, left: 0, bottom: 10 }}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                  <XAxis dataKey="month" stroke="#94a3b8" fontSize={11} tickLine={false} />
                  <YAxis
                    stroke="#94a3b8"
                    fontSize={11}
                    tickLine={false}
                    tickFormatter={(v) => `R$ ${(v / 1000).toFixed(0)}k`}
                  />
                  <Tooltip
                    formatter={(val) => [formatCurrency(Number(val) || 0), 'Receita']}
                    contentStyle={{ backgroundColor: '#0f172a', borderColor: '#334155', borderRadius: '0.75rem', color: '#fff', fontSize: '12px' }}
                  />
                  <Bar dataKey="revenue" fill="#3b82f6" radius={[6, 6, 0, 0]} name="Receita Fechada" />
                </BarChart>
              </ResponsiveContainer>
            )}
          </div>
        </div>

        {/* Top Sellers Table */}
        <div className="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs flex flex-col justify-between">
          <div>
            <div className="flex items-center gap-2 mb-4">
              <Award className="w-5 h-5 text-amber-500" />
              <h3 className="text-base font-bold text-slate-900">Top Vendedores</h3>
            </div>
            <p className="text-xs text-slate-500 mb-4">Classificação por faturamento gerado</p>

            <div className="space-y-3">
              {!report?.topSellers || report.topSellers.length === 0 ? (
                <div className="py-8 text-center text-slate-400 text-sm">
                  Nenhum fechamento registrado por vendedor
                </div>
              ) : (
                report.topSellers.map((seller, idx) => (
                  <div
                    key={idx}
                    className="p-3 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between"
                  >
                    <div className="flex items-center gap-3">
                      <span className="w-6 h-6 rounded-full bg-blue-100 text-blue-700 font-bold text-xs flex items-center justify-center">
                        {idx + 1}
                      </span>
                      <div>
                        <p className="text-xs font-semibold text-slate-900">{seller.sellerName}</p>
                        <p className="text-[11px] text-slate-500">{seller.wonDealsCount} negócios ganhos</p>
                      </div>
                    </div>
                    <span className="text-xs font-bold text-emerald-600">
                      {formatCurrency(seller.totalRevenue)}
                    </span>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="pt-4 border-t border-slate-100 mt-4 text-center">
            <span className="text-xs text-slate-400">
              Dados atualizados com isolamento multi-tenant
            </span>
          </div>
        </div>
      </div>
    </div>
  )
}
export default ReportsPage
