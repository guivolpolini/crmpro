import React, { useState, useEffect } from 'react'
import { api } from '@/lib/api'
import { Deal } from '@/types/pipeline'
import { Briefcase, Search, DollarSign, Building, User, Loader2, Calendar } from 'lucide-react'

export const DealsListPage: React.FC = () => {
  const [deals, setDeals] = useState<Deal[]>([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')

  const fetchDeals = async () => {
    setLoading(true)
    try {
      const res = await api.get('/deals/kanban')
      setDeals(res.data.data || [])
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchDeals()
  }, [])

  const filteredDeals = deals.filter(
    (d) =>
      d.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (d.companyName && d.companyName.toLowerCase().includes(searchTerm.toLowerCase()))
  )

  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val || 0)
  }

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div>
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
          <Briefcase className="w-6 h-6 text-blue-600" />
          Listagem de Oportunidades & Negócios
        </h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Tabela analítica com todas as prospecções e contratos do sistema
        </p>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs flex items-center justify-between">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2 pointer-events-none" />
          <input
            type="text"
            placeholder="Buscar por título ou empresa..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 focus:bg-white text-slate-900"
          />
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
        {loading ? (
          <div className="p-12 flex justify-center items-center text-slate-400 gap-2">
            <Loader2 className="w-6 h-6 animate-spin text-blue-600" />
            <span className="text-sm font-medium">Carregando oportunidades...</span>
          </div>
        ) : filteredDeals.length === 0 ? (
          <div className="p-12 text-center">
            <Briefcase className="w-12 h-12 text-slate-300 mx-auto mb-3" />
            <h3 className="text-base font-semibold text-slate-800">Nenhum negócio encontrado</h3>
            <p className="text-xs text-slate-500 mt-1">Crie oportunidades no funil Kanban ou converta leads.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50/80 text-xs font-semibold text-slate-500 uppercase border-b border-slate-200/80">
                <tr>
                  <th className="px-6 py-4">Título da Oportunidade</th>
                  <th className="px-6 py-4">Empresa / Contato</th>
                  <th className="px-6 py-4">Valor</th>
                  <th className="px-6 py-4">Estágio</th>
                  <th className="px-6 py-4">Probabilidade</th>
                  <th className="px-6 py-4">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredDeals.map((deal) => (
                  <tr key={deal.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-semibold text-slate-900">{deal.title}</div>
                      {deal.expectedCloseDate && (
                        <div className="text-xs text-slate-400 flex items-center gap-1 mt-0.5">
                          <Calendar className="w-3 h-3" />
                          Fechamento previsto: {deal.expectedCloseDate}
                        </div>
                      )}
                    </td>

                    <td className="px-6 py-4">
                      {deal.companyName && (
                        <div className="flex items-center gap-1.5 font-medium text-slate-800">
                          <Building className="w-3.5 h-3.5 text-slate-400" />
                          {deal.companyName}
                        </div>
                      )}
                      {deal.contactName && (
                        <div className="text-xs text-slate-400 flex items-center gap-1 mt-0.5">
                          <User className="w-3 h-3" />
                          {deal.contactName}
                        </div>
                      )}
                    </td>

                    <td className="px-6 py-4 font-bold text-slate-900">
                      {formatCurrency(deal.amount)}
                    </td>

                    <td className="px-6 py-4">
                      <span className="px-2.5 py-1 text-xs font-semibold rounded-md bg-blue-50 text-blue-700 border border-blue-200">
                        {deal.stageName || 'Estágio'}
                      </span>
                    </td>

                    <td className="px-6 py-4 font-semibold text-xs text-slate-700">
                      {deal.probability}%
                    </td>

                    <td className="px-6 py-4">
                      <span
                        className={`px-2.5 py-0.5 text-xs font-semibold rounded-full border ${
                          deal.status === 'WON'
                            ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                            : deal.status === 'LOST'
                            ? 'bg-red-50 text-red-700 border-red-200'
                            : 'bg-blue-50 text-blue-700 border-blue-200'
                        }`}
                      >
                        {deal.status === 'WON' ? 'Ganho' : deal.status === 'LOST' ? 'Perdido' : 'Aberto'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}
