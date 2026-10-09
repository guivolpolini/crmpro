import React, { useState, useEffect } from 'react'
import { api } from '@/lib/api'
import { Company } from '@/types/crm'
import {
  Building2,
  Plus,
  Search,
  Globe,
  MapPin,
  Mail,
  Phone,
  Loader2,
  CheckCircle2,
  X
} from 'lucide-react'

export const CompaniesPage: React.FC = () => {
  const [companies, setCompanies] = useState<Company[]>([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [actionSuccessMessage, setActionSuccessMessage] = useState<string | null>(null)

  const [newCompany, setNewCompany] = useState({
    name: '',
    tradeName: '',
    document: '',
    segment: '',
    website: '',
    phone: '',
    email: '',
    city: '',
    state: ''
  })

  const fetchCompanies = async () => {
    setLoading(true)
    try {
      let url = '/companies?size=50'
      if (searchTerm) url += `&search=${encodeURIComponent(searchTerm)}`
      const res = await api.get(url)
      setCompanies(res.data.data.content || [])
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchCompanies()
  }, [searchTerm])

  const handleCreateCompany = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await api.post('/companies', newCompany)
      setIsModalOpen(false)
      setNewCompany({
        name: '',
        tradeName: '',
        document: '',
        segment: '',
        website: '',
        phone: '',
        email: '',
        city: '',
        state: ''
      })
      setActionSuccessMessage('Empresa cadastrada com sucesso!')
      fetchCompanies()
      setTimeout(() => setActionSuccessMessage(null), 4000)
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao cadastrar empresa')
    }
  }

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <Building2 className="w-6 h-6 text-blue-600" />
            Empresas Clientes & Contas
          </h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Cadastro institucional, segmentos de mercado e endereços das contas
          </p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          className="py-2.5 px-4 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded-xl shadow-md shadow-blue-500/20 flex items-center gap-2 transition-all cursor-pointer"
        >
          <Plus className="w-4 h-4" />
          <span>Nova Empresa</span>
        </button>
      </div>

      {actionSuccessMessage && (
        <div className="p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-sm font-medium flex items-center gap-2.5 shadow-xs">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
          <span>{actionSuccessMessage}</span>
        </div>
      )}

      {/* Filters bar */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs flex items-center justify-between">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2 pointer-events-none" />
          <input
            type="text"
            placeholder="Buscar por razão social ou nome fantasia..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 focus:bg-white text-slate-900"
          />
        </div>
      </div>

      {/* Table Card */}
      <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
        {loading ? (
          <div className="p-12 flex justify-center items-center text-slate-400 gap-2">
            <Loader2 className="w-6 h-6 animate-spin text-blue-600" />
            <span className="text-sm font-medium">Carregando empresas...</span>
          </div>
        ) : companies.length === 0 ? (
          <div className="p-12 text-center">
            <Building2 className="w-12 h-12 text-slate-300 mx-auto mb-3" />
            <h3 className="text-base font-semibold text-slate-800">Nenhuma empresa encontrada</h3>
            <p className="text-xs text-slate-500 mt-1 max-w-sm mx-auto">
              Cadastre contas corporativas ou converta seus leads qualificados.
            </p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50/80 text-xs font-semibold text-slate-500 uppercase border-b border-slate-200/80">
                <tr>
                  <th className="px-6 py-4">Empresa / Razão Social</th>
                  <th className="px-6 py-4">CNPJ / Documento</th>
                  <th className="px-6 py-4">Segmento</th>
                  <th className="px-6 py-4">Localização</th>
                  <th className="px-6 py-4">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {companies.map((comp) => (
                  <tr key={comp.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-semibold text-slate-900">{comp.name}</div>
                      {comp.tradeName && (
                        <div className="text-xs text-slate-500 mt-0.5">Fantasia: {comp.tradeName}</div>
                      )}
                      <div className="flex items-center gap-3 text-xs text-slate-400 mt-1">
                        {comp.website && (
                          <span className="flex items-center gap-1 text-blue-600">
                            <Globe className="w-3 h-3" />
                            {comp.website}
                          </span>
                        )}
                        {comp.phone && (
                          <span className="flex items-center gap-1">
                            <Phone className="w-3 h-3" />
                            {comp.phone}
                          </span>
                        )}
                      </div>
                    </td>

                    <td className="px-6 py-4 font-mono text-xs text-slate-700">
                      {comp.document || <span className="text-slate-400 italic">Não informado</span>}
                    </td>

                    <td className="px-6 py-4">
                      {comp.segment ? (
                        <span className="px-2.5 py-1 text-xs font-medium rounded-md bg-slate-100 text-slate-700">
                          {comp.segment}
                        </span>
                      ) : (
                        <span className="text-xs text-slate-400">-</span>
                      )}
                    </td>

                    <td className="px-6 py-4 text-xs text-slate-600">
                      {comp.city || comp.state ? (
                        <span className="flex items-center gap-1">
                          <MapPin className="w-3.5 h-3.5 text-slate-400" />
                          {[comp.city, comp.state].filter(Boolean).join(' - ')}
                        </span>
                      ) : (
                        <span className="text-slate-400">-</span>
                      )}
                    </td>

                    <td className="px-6 py-4">
                      <span className="px-2.5 py-0.5 text-xs font-semibold rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200">
                        {comp.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* New Company Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-100">
            <div className="flex justify-between items-center mb-5 pb-3 border-b border-slate-100">
              <h3 className="text-lg font-bold text-slate-900">Nova Empresa Cliente</h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 p-1"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateCompany} className="space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Razão Social *
                  </label>
                  <input
                    required
                    placeholder="Ex: Alfa Logística S.A."
                    value={newCompany.name}
                    onChange={(e) => setNewCompany({ ...newCompany, name: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Nome Fantasia
                  </label>
                  <input
                    placeholder="Ex: Alfa Log"
                    value={newCompany.tradeName}
                    onChange={(e) => setNewCompany({ ...newCompany, tradeName: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    CNPJ / Documento
                  </label>
                  <input
                    placeholder="00.000.000/0001-00"
                    value={newCompany.document}
                    onChange={(e) => setNewCompany({ ...newCompany, document: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Segmento / Setor
                  </label>
                  <input
                    placeholder="Ex: Logística e Transporte"
                    value={newCompany.segment}
                    onChange={(e) => setNewCompany({ ...newCompany, segment: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Website
                  </label>
                  <input
                    placeholder="https://empresa.com.br"
                    value={newCompany.website}
                    onChange={(e) => setNewCompany({ ...newCompany, website: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Telefone
                  </label>
                  <input
                    placeholder="(11) 3333-2222"
                    value={newCompany.phone}
                    onChange={(e) => setNewCompany({ ...newCompany, phone: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Cidade
                  </label>
                  <input
                    placeholder="São Paulo"
                    value={newCompany.city}
                    onChange={(e) => setNewCompany({ ...newCompany, city: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Estado (UF)
                  </label>
                  <input
                    placeholder="SP"
                    value={newCompany.state}
                    onChange={(e) => setNewCompany({ ...newCompany, state: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>
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
                  Salvar Empresa
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
