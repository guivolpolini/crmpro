import React, { useState, useEffect } from 'react'
import { api } from '@/lib/api'
import { Proposal, Product } from '@/types/operations'
import { Deal } from '@/types/pipeline'
import {
  FileSpreadsheet,
  Plus,
  Package,
  DollarSign,
  Calendar,
  CheckCircle2,
  Loader2,
  X,
  Tag
} from 'lucide-react'

export const ProposalsPage: React.FC = () => {
  const [proposals, setProposals] = useState<Proposal[]>([])
  const [products, setProducts] = useState<Product[]>([])
  const [deals, setDeals] = useState<Deal[]>([])
  const [loading, setLoading] = useState(true)
  const [activeTab, setActiveTab] = useState<'proposals' | 'products'>('proposals')
  const [isProposalModalOpen, setIsProposalModalOpen] = useState(false)
  const [isProductModalOpen, setIsProductModalOpen] = useState(false)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  const [newProposal, setNewProposal] = useState({
    dealId: '',
    totalAmount: 50000,
    discount: 0,
    validUntil: '',
    notes: ''
  })

  const [newProduct, setNewProduct] = useState({
    name: '',
    code: '',
    description: '',
    unitPrice: 1000,
    unit: 'UN'
  })

  const fetchData = async () => {
    setLoading(true)
    try {
      const [propsRes, prodsRes, dealsRes] = await Promise.all([
        api.get('/proposals'),
        api.get('/proposals/products'),
        api.get('/deals/kanban')
      ])
      setProposals(propsRes.data.data || [])
      setProducts(prodsRes.data.data || [])
      setDeals(dealsRes.data.data || [])

      if (dealsRes.data.data?.length > 0 && !newProposal.dealId) {
        setNewProposal((prev) => ({ ...prev, dealId: dealsRes.data.data[0].id }))
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

  const handleCreateProposal = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await api.post('/proposals', {
        ...newProposal,
        validUntil: newProposal.validUntil || null
      })
      setIsProposalModalOpen(false)
      setSuccessMessage('Proposta comercial emitida com sucesso!')
      fetchData()
      setTimeout(() => setSuccessMessage(null), 4000)
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao emitir proposta')
    }
  }

  const handleCreateProduct = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await api.post('/proposals/products', newProduct)
      setIsProductModalOpen(false)
      setNewProduct({
        name: '',
        code: '',
        description: '',
        unitPrice: 1000,
        unit: 'UN'
      })
      setSuccessMessage('Produto adicionado ao catálogo!')
      fetchData()
      setTimeout(() => setSuccessMessage(null), 4000)
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao cadastrar produto')
    }
  }

  const handleUpdateProposalStatus = async (propId: string, status: string) => {
    try {
      await api.patch(`/proposals/${propId}/status?status=${status}`)
      fetchData()
    } catch (err) {
      console.error(err)
    }
  }

  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val || 0)
  }

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <FileSpreadsheet className="w-6 h-6 text-blue-600" />
            Propostas Comerciais & Catálogo
          </h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Gere orçamentos formais vinculados a negociações e controle produtos
          </p>
        </div>

        <div className="flex items-center gap-2">
          {activeTab === 'proposals' ? (
            <button
              onClick={() => setIsProposalModalOpen(true)}
              className="py-2.5 px-4 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded-xl shadow-md shadow-blue-500/20 flex items-center gap-2 transition-all cursor-pointer"
            >
              <Plus className="w-4 h-4" />
              <span>Emitir Proposta</span>
            </button>
          ) : (
            <button
              onClick={() => setIsProductModalOpen(true)}
              className="py-2.5 px-4 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded-xl shadow-md shadow-blue-500/20 flex items-center gap-2 transition-all cursor-pointer"
            >
              <Plus className="w-4 h-4" />
              <span>Novo Produto / Serviço</span>
            </button>
          )}
        </div>
      </div>

      {successMessage && (
        <div className="p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-sm font-medium flex items-center gap-2.5 shadow-xs">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
          <span>{successMessage}</span>
        </div>
      )}

      {/* Tabs */}
      <div className="flex items-center gap-2 border-b border-slate-200">
        <button
          onClick={() => setActiveTab('proposals')}
          className={`pb-3 px-3 text-sm font-bold border-b-2 transition-all cursor-pointer ${
            activeTab === 'proposals'
              ? 'border-blue-600 text-blue-600'
              : 'border-transparent text-slate-500 hover:text-slate-800'
          }`}
        >
          Propostas Emitidas ({proposals.length})
        </button>

        <button
          onClick={() => setActiveTab('products')}
          className={`pb-3 px-3 text-sm font-bold border-b-2 transition-all cursor-pointer ${
            activeTab === 'products'
              ? 'border-blue-600 text-blue-600'
              : 'border-transparent text-slate-500 hover:text-slate-800'
          }`}
        >
          Catálogo de Produtos & Serviços ({products.length})
        </button>
      </div>

      {/* Content Area */}
      {loading ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-16 flex justify-center items-center gap-2 text-slate-500">
          <Loader2 className="w-6 h-6 animate-spin text-blue-600" />
          <span className="text-sm font-medium">Carregando propostas...</span>
        </div>
      ) : activeTab === 'proposals' ? (
        <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
          {proposals.length === 0 ? (
            <div className="p-12 text-center">
              <FileSpreadsheet className="w-12 h-12 text-slate-300 mx-auto mb-3" />
              <h3 className="text-base font-semibold text-slate-800">Nenhuma proposta emitida</h3>
              <p className="text-xs text-slate-500 mt-1">Gere sua primeira proposta vinculada a uma negociação.</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm text-slate-600">
                <thead className="bg-slate-50/80 text-xs font-semibold text-slate-500 uppercase border-b border-slate-200/80">
                  <tr>
                    <th className="px-6 py-4">Código / Identificador</th>
                    <th className="px-6 py-4">Negócio Vinculado</th>
                    <th className="px-6 py-4">Valor Total</th>
                    <th className="px-6 py-4">Validade</th>
                    <th className="px-6 py-4">Situação</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {proposals.map((prop) => (
                    <tr key={prop.id} className="hover:bg-slate-50/80 transition-colors">
                      <td className="px-6 py-4 font-mono font-bold text-slate-900">
                        {prop.code}
                      </td>

                      <td className="px-6 py-4">
                        <div className="font-semibold text-slate-900">{prop.dealTitle || 'Negócio'}</div>
                        {prop.companyName && (
                          <div className="text-xs text-slate-500 mt-0.5">{prop.companyName}</div>
                        )}
                      </td>

                      <td className="px-6 py-4 font-extrabold text-slate-900">
                        {formatCurrency(prop.totalAmount)}
                      </td>

                      <td className="px-6 py-4 text-xs text-slate-500">
                        {prop.validUntil || 'Indeterminada'}
                      </td>

                      <td className="px-6 py-4">
                        <select
                          value={prop.status}
                          onChange={(e) => handleUpdateProposalStatus(prop.id, e.target.value)}
                          className="text-xs px-2.5 py-1 rounded-lg border font-semibold bg-white cursor-pointer"
                        >
                          <option value="DRAFT">Rascunho</option>
                          <option value="SENT">Enviada</option>
                          <option value="ACCEPTED">Aprovada / Aceita</option>
                          <option value="REJECTED">Recusada</option>
                        </select>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      ) : (
        /* Products Catalog Grid */
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {products.map((prod) => (
            <div
              key={prod.id}
              className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex flex-col justify-between"
            >
              <div>
                <div className="flex items-center justify-between mb-2">
                  <span className="text-[11px] font-mono px-2 py-0.5 rounded bg-slate-100 text-slate-600 font-semibold">
                    {prod.code || 'CAT-ITEM'}
                  </span>
                  <span className="text-[11px] font-bold px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-700">
                    Ativo
                  </span>
                </div>
                <h4 className="font-bold text-base text-slate-900">{prod.name}</h4>
                {prod.description && (
                  <p className="text-xs text-slate-500 mt-1 line-clamp-2">{prod.description}</p>
                )}
              </div>

              <div className="mt-4 pt-4 border-t border-slate-100 flex items-center justify-between">
                <span className="text-xs text-slate-400">Preço Unitário:</span>
                <span className="text-base font-extrabold text-slate-900">
                  {formatCurrency(prod.unitPrice)}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* New Proposal Modal */}
      {isProposalModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-100">
            <div className="flex justify-between items-center mb-5 pb-3 border-b border-slate-100">
              <h3 className="text-lg font-bold text-slate-900">Emitir Proposta Comercial</h3>
              <button
                onClick={() => setIsProposalModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 p-1"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateProposal} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  Negociação Vinculada *
                </label>
                <select
                  required
                  value={newProposal.dealId}
                  onChange={(e) => setNewProposal({ ...newProposal, dealId: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-800"
                >
                  {deals.map((d) => (
                    <option key={d.id} value={d.id}>
                      {d.title} ({formatCurrency(d.amount)})
                    </option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Valor Total (R$) *
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={newProposal.totalAmount}
                    onChange={(e) => setNewProposal({ ...newProposal, totalAmount: parseFloat(e.target.value) || 0 })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900 font-bold"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Desconto (R$)
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    value={newProposal.discount}
                    onChange={(e) => setNewProposal({ ...newProposal, discount: parseFloat(e.target.value) || 0 })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  Data de Validade
                </label>
                <input
                  type="date"
                  value={newProposal.validUntil}
                  onChange={(e) => setNewProposal({ ...newProposal, validUntil: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  Observações / Condições Comerciais
                </label>
                <textarea
                  rows={2}
                  placeholder="Ex: Faturamento 30 dias..."
                  value={newProposal.notes}
                  onChange={(e) => setNewProposal({ ...newProposal, notes: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900 resize-none"
                />
              </div>

              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setIsProposalModalOpen(false)}
                  className="px-4 py-2 border border-slate-200 text-slate-600 text-xs font-semibold rounded-xl hover:bg-slate-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold rounded-xl shadow-md shadow-blue-500/20"
                >
                  Salvar Proposta
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* New Product Modal */}
      {isProductModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-100">
            <div className="flex justify-between items-center mb-5 pb-3 border-b border-slate-100">
              <h3 className="text-lg font-bold text-slate-900">Novo Produto / Serviço</h3>
              <button
                onClick={() => setIsProductModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 p-1"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateProduct} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  Nome do Item *
                </label>
                <input
                  required
                  placeholder="Ex: Consultoria em TI (Hora)"
                  value={newProduct.name}
                  onChange={(e) => setNewProduct({ ...newProduct, name: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Código / SKU
                  </label>
                  <input
                    placeholder="SRV-01"
                    value={newProduct.code}
                    onChange={(e) => setNewProduct({ ...newProduct, code: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Preço Unitário (R$) *
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={newProduct.unitPrice}
                    onChange={(e) => setNewProduct({ ...newProduct, unitPrice: parseFloat(e.target.value) || 0 })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900 font-bold"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  Descrição
                </label>
                <textarea
                  rows={2}
                  placeholder="Especificações do produto..."
                  value={newProduct.description}
                  onChange={(e) => setNewProduct({ ...newProduct, description: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900 resize-none"
                />
              </div>

              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setIsProductModalOpen(false)}
                  className="px-4 py-2 border border-slate-200 text-slate-600 text-xs font-semibold rounded-xl hover:bg-slate-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold rounded-xl shadow-md shadow-blue-500/20"
                >
                  Salvar Item
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
