import React, { useState, useEffect } from 'react'
import { api } from '@/lib/api'
import { PipelineStage, Deal } from '@/types/pipeline'
import { Company, Contact } from '@/types/crm'
import {
  Kanban,
  Plus,
  DollarSign,
  Building,
  User,
  ArrowRight,
  TrendingUp,
  Loader2,
  CheckCircle2,
  X,
  Calendar
} from 'lucide-react'

export const KanbanBoardPage: React.FC = () => {
  const [stages, setStages] = useState<PipelineStage[]>([])
  const [deals, setDeals] = useState<Deal[]>([])
  const [companies, setCompanies] = useState<Company[]>([])
  const [contacts, setContacts] = useState<Contact[]>([])
  const [loading, setLoading] = useState(true)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [movingDealId, setMovingDealId] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  const [newDeal, setNewDeal] = useState({
    title: '',
    stageId: '',
    companyId: '',
    contactId: '',
    amount: 10000,
    probability: 50,
    expectedCloseDate: ''
  })

  const fetchData = async () => {
    setLoading(true)
    try {
      const [stagesRes, dealsRes, compRes, contRes] = await Promise.all([
        api.get('/pipeline-stages'),
        api.get('/deals/kanban'),
        api.get('/companies?size=100'),
        api.get('/contacts?size=100')
      ])
      setStages(stagesRes.data.data || [])
      setDeals(dealsRes.data.data || [])
      setCompanies(compRes.data.data.content || [])
      setContacts(contRes.data.data.content || [])

      if (stagesRes.data.data?.length > 0 && !newDeal.stageId) {
        setNewDeal((prev) => ({ ...prev, stageId: stagesRes.data.data[0].id }))
      }
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData()
  }, [])

  const handleCreateDeal = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await api.post('/deals', {
        ...newDeal,
        companyId: newDeal.companyId || null,
        contactId: newDeal.contactId || null,
        expectedCloseDate: newDeal.expectedCloseDate || null
      })
      setIsModalOpen(false)
      setNewDeal({
        title: '',
        stageId: stages[0]?.id || '',
        companyId: '',
        contactId: '',
        amount: 10000,
        probability: 50,
        expectedCloseDate: ''
      })
      setSuccessMessage('Oportunidade cadastrada no funil com sucesso!')
      fetchData()
      setTimeout(() => setSuccessMessage(null), 4000)
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao criar oportunidade')
    }
  }

  const handleMoveStage = async (dealId: string, targetStageId: string) => {
    setMovingDealId(dealId)
    try {
      await api.patch(`/deals/${dealId}/move`, { targetStageId })
      setSuccessMessage('Oportunidade movida de estágio!')
      fetchData()
      setTimeout(() => setSuccessMessage(null), 3000)
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao mover estágio')
    } finally {
      setMovingDealId(null)
    }
  }

  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val || 0)
  }

  const totalPipelineValue = deals.reduce((acc, d) => acc + (d.amount || 0), 0)

  return (
    <div className="space-y-6 max-w-[1600px] mx-auto">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <Kanban className="w-6 h-6 text-blue-600" />
            Funil de Vendas — Quadro Kanban
          </h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Acompanhe o progresso de cada oportunidade em tempo real com valor ponderado
          </p>
        </div>

        <div className="flex items-center gap-3">
          <div className="bg-white border border-slate-200/80 px-4 py-2 rounded-xl text-xs font-medium text-slate-600 flex items-center gap-2 shadow-xs">
            <span>Valor Total em Negociação:</span>
            <span className="font-bold text-slate-900 text-sm">{formatCurrency(totalPipelineValue)}</span>
          </div>

          <button
            onClick={() => setIsModalOpen(true)}
            className="py-2.5 px-4 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded-xl shadow-md shadow-blue-500/20 flex items-center gap-2 transition-all cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            <span>Nova Oportunidade</span>
          </button>
        </div>
      </div>

      {successMessage && (
        <div className="p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-sm font-medium flex items-center gap-2.5 shadow-xs">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
          <span>{successMessage}</span>
        </div>
      )}

      {/* Kanban Board Columns Container */}
      {loading ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-16 flex justify-center items-center gap-2 text-slate-500">
          <Loader2 className="w-6 h-6 animate-spin text-blue-600" />
          <span className="text-sm font-medium">Carregando quadro de oportunidades...</span>
        </div>
      ) : (
        <div className="flex gap-4 overflow-x-auto pb-6 items-start min-h-[650px]">
          {stages.map((stage) => {
            const stageDeals = deals.filter((d) => d.stageId === stage.id)
            const stageTotal = stageDeals.reduce((sum, d) => sum + (d.amount || 0), 0)

            return (
              <div
                key={stage.id}
                className="w-80 shrink-0 bg-slate-100/80 rounded-2xl border border-slate-200/90 flex flex-col max-h-[800px]"
              >
                {/* Column Header */}
                <div className="p-4 border-b border-slate-200/80 bg-white/60 rounded-t-2xl">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <span
                        className="w-3 h-3 rounded-full shrink-0"
                        style={{ backgroundColor: stage.color || '#3b82f6' }}
                      />
                      <h3 className="font-bold text-sm text-slate-900 truncate">{stage.name}</h3>
                    </div>
                    <span className="text-xs font-bold px-2 py-0.5 rounded-full bg-slate-200/80 text-slate-700">
                      {stageDeals.length}
                    </span>
                  </div>
                  <div className="text-xs font-semibold text-slate-500 mt-2">
                    Total: <span className="text-slate-800">{formatCurrency(stageTotal)}</span>
                  </div>
                </div>

                {/* Column Cards */}
                <div className="p-3 space-y-3 overflow-y-auto flex-1">
                  {stageDeals.length === 0 ? (
                    <div className="p-6 text-center text-xs text-slate-400 italic border border-dashed border-slate-200 rounded-xl bg-white/30">
                      Nenhum negócio neste estágio
                    </div>
                  ) : (
                    stageDeals.map((deal) => (
                      <div
                        key={deal.id}
                        className="bg-white p-4 rounded-xl border border-slate-200/80 shadow-xs hover:shadow-md transition-all flex flex-col justify-between"
                      >
                        <div>
                          <div className="flex items-start justify-between gap-2 mb-2">
                            <h4 className="font-bold text-sm text-slate-900 line-clamp-2">
                              {deal.title}
                            </h4>
                            <span className="text-[11px] font-bold px-2 py-0.5 rounded bg-blue-50 text-blue-700 shrink-0">
                              {deal.probability}%
                            </span>
                          </div>

                          <div className="text-base font-extrabold text-slate-900 mb-3 flex items-center gap-1">
                            <DollarSign className="w-4 h-4 text-emerald-600" />
                            {formatCurrency(deal.amount)}
                          </div>

                          <div className="space-y-1.5 text-xs text-slate-500 pt-2 border-t border-slate-100">
                            {deal.companyName && (
                              <div className="flex items-center gap-1.5 text-slate-700 font-medium truncate">
                                <Building className="w-3.5 h-3.5 text-slate-400 shrink-0" />
                                <span className="truncate">{deal.companyName}</span>
                              </div>
                            )}

                            {deal.contactName && (
                              <div className="flex items-center gap-1.5 truncate">
                                <User className="w-3.5 h-3.5 text-slate-400 shrink-0" />
                                <span className="truncate">{deal.contactName}</span>
                              </div>
                            )}

                            {deal.expectedCloseDate && (
                              <div className="flex items-center gap-1.5 text-slate-400">
                                <Calendar className="w-3.5 h-3.5 shrink-0" />
                                <span>Previsão: {deal.expectedCloseDate}</span>
                              </div>
                            )}
                          </div>
                        </div>

                        {/* Move Stage Selector */}
                        <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
                          <span className="text-[11px] font-semibold text-slate-400 uppercase">Mover:</span>
                          <select
                            value={deal.stageId}
                            disabled={movingDealId === deal.id}
                            onChange={(e) => handleMoveStage(deal.id, e.target.value)}
                            className="text-xs bg-slate-50 hover:bg-slate-100 border border-slate-200 rounded-lg px-2 py-1 text-slate-700 font-medium focus:outline-none cursor-pointer"
                          >
                            {stages.map((s) => (
                              <option key={s.id} value={s.id}>
                                {s.name}
                              </option>
                            ))}
                          </select>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            )
          })}
        </div>
      )}

      {/* New Deal Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-100">
            <div className="flex justify-between items-center mb-5 pb-3 border-b border-slate-100">
              <h3 className="text-lg font-bold text-slate-900">Nova Oportunidade Comercial</h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 p-1"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateDeal} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  Título da Negociação *
                </label>
                <input
                  required
                  placeholder="Ex: Fornecimento de Software CRM PRO"
                  value={newDeal.title}
                  onChange={(e) => setNewDeal({ ...newDeal, title: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Valor Estimado (R$) *
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={newDeal.amount}
                    onChange={(e) => setNewDeal({ ...newDeal, amount: parseFloat(e.target.value) || 0 })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900 font-semibold"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Probabilidade (%)
                  </label>
                  <input
                    type="number"
                    min="0"
                    max="100"
                    value={newDeal.probability}
                    onChange={(e) => setNewDeal({ ...newDeal, probability: parseInt(e.target.value) || 0 })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  Estágio Inicial *
                </label>
                <select
                  required
                  value={newDeal.stageId}
                  onChange={(e) => setNewDeal({ ...newDeal, stageId: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-800"
                >
                  {stages.map((s) => (
                    <option key={s.id} value={s.id}>
                      {s.name}
                    </option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Empresa Cliente
                  </label>
                  <select
                    value={newDeal.companyId}
                    onChange={(e) => setNewDeal({ ...newDeal, companyId: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-800"
                  >
                    <option value="">Selecione...</option>
                    {companies.map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.name}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Contato Principal
                  </label>
                  <select
                    value={newDeal.contactId}
                    onChange={(e) => setNewDeal({ ...newDeal, contactId: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-800"
                  >
                    <option value="">Selecione...</option>
                    {contacts.map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  Previsão de Fechamento
                </label>
                <input
                  type="date"
                  value={newDeal.expectedCloseDate}
                  onChange={(e) => setNewDeal({ ...newDeal, expectedCloseDate: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
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
                  Criar Oportunidade
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
