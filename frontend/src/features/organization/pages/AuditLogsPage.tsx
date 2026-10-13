import React, { useState, useEffect } from 'react'
import { api } from '@/lib/api'
import {
  ShieldAlert,
  Clock,
  User,
  Activity,
  Layers,
  ChevronLeft,
  ChevronRight,
  RefreshCw
} from 'lucide-react'

interface AuditLogItem {
  id: string;
  organizationId: string;
  userId: string | null;
  userEmail: string;
  action: string;
  entityType: string;
  entityId: string;
  description: string;
  createdAt: string;
}

export const AuditLogsPage: React.FC = () => {
  const [logs, setLogs] = useState<AuditLogItem[]>([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(1)

  const fetchLogs = async () => {
    setLoading(true)
    try {
      const res = await api.get(`/audit-logs?page=${page}&size=15`)
      setLogs(res.data.data.content || [])
      setTotalPages(res.data.data.totalPages || 1)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchLogs()
  }, [page])

  const getActionBadge = (action: string) => {
    const map: Record<string, { bg: string; text: string }> = {
      CREATE: { bg: 'bg-emerald-50 border-emerald-200', text: 'text-emerald-700' },
      UPDATE: { bg: 'bg-blue-50 border-blue-200', text: 'text-blue-700' },
      STAGE_TRANSITION: { bg: 'bg-purple-50 border-purple-200', text: 'text-purple-700' },
      DELETE: { bg: 'bg-rose-50 border-rose-200', text: 'text-rose-700' },
      CONVERT: { bg: 'bg-amber-50 border-amber-200', text: 'text-amber-700' }
    }
    const item = map[action] || { bg: 'bg-slate-50 border-slate-200', text: 'text-slate-700' }
    return (
      <span className={`px-2 py-0.5 text-xs font-semibold rounded-full border ${item.bg} ${item.text}`}>
        {action}
      </span>
    )
  }

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <ShieldAlert className="w-6 h-6 text-purple-600" />
            Trilha de Auditoria & Governança
          </h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Registro imutável de ações críticas, transições no pipeline e compliance multi-tenant
          </p>
        </div>

        <button
          onClick={fetchLogs}
          className="py-2.5 px-4 bg-white border border-slate-200 hover:bg-slate-50 text-slate-700 text-sm font-semibold rounded-xl shadow-xs flex items-center gap-2 transition-all cursor-pointer"
        >
          <RefreshCw className="w-4 h-4 text-slate-500" />
          <span>Atualizar</span>
        </button>
      </div>

      {/* Table Container */}
      <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
        {loading ? (
          <div className="p-12 text-center text-slate-400">Carregando trilha de auditoria...</div>
        ) : logs.length === 0 ? (
          <div className="p-12 text-center text-slate-500">
            Nenhuma atividade auditada registrada até o momento.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse text-sm">
              <thead>
                <tr className="bg-slate-50/70 border-b border-slate-200/80 text-xs font-bold uppercase tracking-wider text-slate-500">
                  <th className="px-6 py-3.5">Data & Hora</th>
                  <th className="px-6 py-3.5">Usuário</th>
                  <th className="px-6 py-3.5">Ação</th>
                  <th className="px-6 py-3.5">Entidade</th>
                  <th className="px-6 py-3.5">Descrição</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {logs.map((log) => (
                  <tr key={log.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="px-6 py-4 text-xs font-mono text-slate-500 whitespace-nowrap">
                      <div className="flex items-center gap-1.5">
                        <Clock className="w-3.5 h-3.5 text-slate-400" />
                        {new Date(log.createdAt).toLocaleString('pt-BR')}
                      </div>
                    </td>

                    <td className="px-6 py-4 whitespace-nowrap font-medium text-slate-800">
                      <div className="flex items-center gap-1.5">
                        <User className="w-3.5 h-3.5 text-slate-400" />
                        {log.userEmail || 'system'}
                      </div>
                    </td>

                    <td className="px-6 py-4 whitespace-nowrap">
                      {getActionBadge(log.action)}
                    </td>

                    <td className="px-6 py-4 whitespace-nowrap text-xs font-bold text-slate-600">
                      <div className="flex items-center gap-1.5">
                        <Layers className="w-3.5 h-3.5 text-slate-400" />
                        {log.entityType}
                      </div>
                    </td>

                    <td className="px-6 py-4 text-xs text-slate-600 max-w-md">
                      {log.description}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination */}
        <div className="p-4 border-t border-slate-200/80 flex items-center justify-between text-xs text-slate-500 bg-slate-50/30">
          <span>Página {page + 1} de {totalPages || 1}</span>
          <div className="flex items-center gap-2">
            <button
              onClick={() => setPage((p) => Math.max(0, p - 1))}
              disabled={page === 0}
              className="p-1.5 rounded-lg border border-slate-200 bg-white hover:bg-slate-50 disabled:opacity-40 cursor-pointer"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <button
              onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
              disabled={page >= totalPages - 1}
              className="p-1.5 rounded-lg border border-slate-200 bg-white hover:bg-slate-50 disabled:opacity-40 cursor-pointer"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
