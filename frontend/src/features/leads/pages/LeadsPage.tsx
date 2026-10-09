import React, { useState, useEffect } from 'react'
import { api } from '@/lib/api'
import { Lead, LeadStatus } from '@/types/crm'
import {
  Users,
  Plus,
  Search,
  ArrowRightCircle,
  Building,
  Mail,
  Phone,
  Sparkles,
  Loader2,
  CheckCircle2,
  X
} from 'lucide-react'

export const LeadsPage: React.FC = () => {
  const [leads, setLeads] = useState<Lead[]>([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')
  const [statusFilter, setStatusFilter] = useState<string>('')
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [convertingId, setConvertingId] = useState<string | null>(null)
  const [actionSuccessMessage, setActionSuccessMessage] = useState<string | null>(null)

  // Form state
  const [newLead, setNewLead] = useState({
    name: '',
    companyName: '',
    email: '',
    phone: '',
    source: 'WEBSITE',
    score: 50,
    notes: ''
  })

  const fetchLeads = async () => {
    setLoading(true)
    try {
      let url = '/leads?size=50'
      if (searchTerm) url += `&search=${encodeURIComponent(searchTerm)}`
      if (statusFilter) url += `&status=${encodeURIComponent(statusFilter)}`
      const res = await api.get(url)
      setLeads(res.data.data.content || [])
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchLeads()
  }, [searchTerm, statusFilter])

  const handleCreateLead = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await api.post('/leads', newLead)
      setIsModalOpen(false)
      setNewLead({
        name: '',
        companyName: '',
        email: '',
        phone: '',
        source: 'WEBSITE',
        score: 50,
        notes: ''
      })
      setActionSuccessMessage('Lead cadastrado com sucesso!')
      fetchLeads()
      setTimeout(() => setActionSuccessMessage(null), 4000)
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao cadastrar lead')
    }
  }

  const handleConvertLead = async (leadId: string) => {
    setConvertingId(leadId)
    try {
      const res = await api.post(`/leads/${leadId}/convert`, { createCompany: true })
      setActionSuccessMessage('Lead convertido em Contato e Empresa com sucesso!')
      fetchLeads()
      setTimeout(() => setActionSuccessMessage(null), 5000)
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao converter lead')
    } finally {
      setConvertingId(null)
    }
  }

  const getStatusBadge = (status: LeadStatus) => {
    const map: Record<LeadStatus, { label: string; bg: string; text: string }> = {
      NEW: { label: 'Novo', bg: 'bg-blue-50', text: 'text-blue-700 border-blue-200' },
      CONTACTED: { label: 'Em Contato', bg: 'bg-amber-50', text: 'text-amber-700 border-amber-200' },
      QUALIFIED: { label: 'Qualificado', bg: 'bg-purple-50', text: 'text-purple-700 border-purple-200' },
      UNQUALIFIED: { label: 'Desqualificado', bg: 'bg-slate-100', text: 'text-slate-600 border-slate-200' },
      CONVERTED: { label: 'Convertido', bg: 'bg-emerald-50', text: 'text-emerald-700 border-emerald-200' }
    }
    const item = map[status] || map.NEW
    return (
      <span className={`px-2.5 py-0.5 text-xs font-semibold rounded-full border ${item.bg} ${item.text}`}>
        {item.label}
      </span>
    )
  }

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <Users className="w-6 h-6 text-blue-600" />
            Gestão de Leads Comerciais
          </h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Capture, qualifique e converta oportunidades em clientes e contatos
          </p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          className="py-2.5 px-4 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded-xl shadow-md shadow-blue-500/20 flex items-center gap-2 transition-all cursor-pointer"
        >
          <Plus className="w-4 h-4" />
          <span>Novo Lead</span>
        </button>
      </div>

      {actionSuccessMessage && (
        <div className="p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-sm font-medium flex items-center gap-2.5 shadow-xs">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
          <span>{actionSuccessMessage}</span>
        </div>
      )}

      {/* Filters bar */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs flex flex-col sm:flex-row gap-3 items-center justify-between">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2 pointer-events-none" />
          <input
            type="text"
            placeholder="Buscar por nome do lead..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 focus:bg-white text-slate-900"
          />
        </div>

        <div className="flex items-center gap-2 w-full sm:w-auto">
          <label className="text-xs font-semibold text-slate-500 uppercase">Status:</label>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-700 cursor-pointer"
          >
            <option value="">Todos</option>
            <option value="NEW">Novo</option>
            <option value="CONTACTED">Em Contato</option>
            <option value="QUALIFIED">Qualificado</option>
            <option value="UNQUALIFIED">Desqualificado</option>
            <option value="CONVERTED">Convertido</option>
          </select>
        </div>
      </div>

      {/* Table Card */}
      <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
        {loading ? (
          <div className="p-12 flex justify-center items-center text-slate-400 gap-2">
            <Loader2 className="w-6 h-6 animate-spin text-blue-600" />
            <span className="text-sm font-medium">Carregando leads...</span>
          </div>
        ) : leads.length === 0 ? (
          <div className="p-12 text-center">
            <Users className="w-12 h-12 text-slate-300 mx-auto mb-3" />
            <h3 className="text-base font-semibold text-slate-800">Nenhum lead encontrado</h3>
            <p className="text-xs text-slate-500 mt-1 max-w-sm mx-auto">
              Cadastre seu primeiro lead ou ajuste os filtros de pesquisa para visualizar os registros.
            </p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50/80 text-xs font-semibold text-slate-500 uppercase border-b border-slate-200/80">
                <tr>
                  <th className="px-6 py-4">Nome & Contato</th>
                  <th className="px-6 py-4">Empresa / Organização</th>
                  <th className="px-6 py-4">Origem</th>
                  <th className="px-6 py-4">Status</th>
                  <th className="px-6 py-4">Score</th>
                  <th className="px-6 py-4 text-right">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 font-normal">
                {leads.map((lead) => (
                  <tr key={lead.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-semibold text-slate-900">{lead.name}</div>
                      <div className="flex items-center gap-3 text-xs text-slate-400 mt-0.5">
                        {lead.email && (
                          <span className="flex items-center gap-1">
                            <Mail className="w-3 h-3" />
                            {lead.email}
                          </span>
                        )}
                        {lead.phone && (
                          <span className="flex items-center gap-1">
                            <Phone className="w-3 h-3" />
                            {lead.phone}
                          </span>
                        )}
                      </div>
                    </td>

                    <td className="px-6 py-4">
                      {lead.companyName ? (
                        <span className="flex items-center gap-1.5 font-medium text-slate-800">
                          <Building className="w-3.5 h-3.5 text-slate-400" />
                          {lead.companyName}
                        </span>
                      ) : (
                        <span className="text-xs text-slate-400 italic">Não informada</span>
                      )}
                    </td>

                    <td className="px-6 py-4">
                      <span className="text-xs font-medium text-slate-600 bg-slate-100 px-2.5 py-1 rounded-md">
                        {lead.source}
                      </span>
                    </td>

                    <td className="px-6 py-4">{getStatusBadge(lead.status)}</td>

                    <td className="px-6 py-4">
                      <div className="flex items-center gap-1.5 font-semibold text-xs text-slate-700">
                        <Sparkles className="w-3.5 h-3.5 text-amber-500" />
                        <span>{lead.score} pts</span>
                      </div>
                    </td>

                    <td className="px-6 py-4 text-right">
                      {lead.status !== 'CONVERTED' ? (
                        <button
                          onClick={() => handleConvertLead(lead.id)}
                          disabled={convertingId === lead.id}
                          className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 border border-emerald-200 text-xs font-semibold rounded-lg transition-colors cursor-pointer disabled:opacity-50"
                        >
                          {convertingId === lead.id ? (
                            <Loader2 className="w-3.5 h-3.5 animate-spin" />
                          ) : (
                            <ArrowRightCircle className="w-3.5 h-3.5" />
                          )}
                          <span>Converter</span>
                        </button>
                      ) : (
                        <span className="text-xs text-emerald-600 font-medium flex items-center justify-end gap-1">
                          <CheckCircle2 className="w-3.5 h-3.5" />
                          Convertido
                        </span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* New Lead Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-100">
            <div className="flex justify-between items-center mb-5 pb-3 border-b border-slate-100">
              <h3 className="text-lg font-bold text-slate-900">Novo Lead Comercial</h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 p-1"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateLead} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  Nome Completo *
                </label>
                <input
                  required
                  placeholder="Ex: Roberto Silva"
                  value={newLead.name}
                  onChange={(e) => setNewLead({ ...newLead, name: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  Empresa / Razão Social
                </label>
                <input
                  placeholder="Ex: Comercial Silva Ltda"
                  value={newLead.companyName}
                  onChange={(e) => setNewLead({ ...newLead, companyName: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    E-mail
                  </label>
                  <input
                    type="email"
                    placeholder="email@empresa.com"
                    value={newLead.email}
                    onChange={(e) => setNewLead({ ...newLead, email: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Telefone
                  </label>
                  <input
                    placeholder="(11) 98888-7777"
                    value={newLead.phone}
                    onChange={(e) => setNewLead({ ...newLead, phone: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Origem
                  </label>
                  <select
                    value={newLead.source}
                    onChange={(e) => setNewLead({ ...newLead, source: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-800"
                  >
                    <option value="WEBSITE">Website</option>
                    <option value="GOOGLE">Google Ads / Busca</option>
                    <option value="INDICATION">Indicação</option>
                    <option value="LINKEDIN">LinkedIn</option>
                    <option value="EVENT">Evento</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Score Inicial (0-100)
                  </label>
                  <input
                    type="number"
                    min="0"
                    max="100"
                    value={newLead.score}
                    onChange={(e) => setNewLead({ ...newLead, score: parseInt(e.target.value) || 0 })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  Notas / Observações
                </label>
                <textarea
                  rows={3}
                  placeholder="Detalhes sobre a necessidade do lead..."
                  value={newLead.notes}
                  onChange={(e) => setNewLead({ ...newLead, notes: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900 resize-none"
                />
              </div>

              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 border border-slate-200 text-slate-600 text-xs font-semibold rounded-xl hover:bg-slate-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold rounded-xl shadow-md shadow-blue-500/20"
                >
                  Salvar Lead
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
