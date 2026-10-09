import React, { useState, useEffect } from 'react'
import { api } from '@/lib/api'
import { Contact, Company } from '@/types/crm'
import {
  Contact2,
  Plus,
  Search,
  Building,
  Mail,
  Phone,
  Briefcase,
  Loader2,
  CheckCircle2,
  X
} from 'lucide-react'

export const ContactsPage: React.FC = () => {
  const [contacts, setContacts] = useState<Contact[]>([])
  const [companies, setCompanies] = useState<Company[]>([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [actionSuccessMessage, setActionSuccessMessage] = useState<string | null>(null)

  const [newContact, setNewContact] = useState({
    name: '',
    companyId: '',
    email: '',
    phone: '',
    jobTitle: '',
    notes: ''
  })

  const fetchContacts = async () => {
    setLoading(true)
    try {
      let url = '/contacts?size=50'
      if (searchTerm) url += `&search=${encodeURIComponent(searchTerm)}`
      const [resContacts, resCompanies] = await Promise.all([
        api.get(url),
        api.get('/companies?size=100')
      ])
      setContacts(resContacts.data.data.content || [])
      setCompanies(resCompanies.data.data.content || [])
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchContacts()
  }, [searchTerm])

  const handleCreateContact = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await api.post('/contacts', {
        ...newContact,
        companyId: newContact.companyId ? newContact.companyId : null
      })
      setIsModalOpen(false)
      setNewContact({
        name: '',
        companyId: '',
        email: '',
        phone: '',
        jobTitle: '',
        notes: ''
      })
      setActionSuccessMessage('Contato cadastrado com sucesso!')
      fetchContacts()
      setTimeout(() => setActionSuccessMessage(null), 4000)
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao cadastrar contato')
    }
  }

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <Contact2 className="w-6 h-6 text-blue-600" />
            Contatos & Decisores
          </h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Mantenha o histórico e canais de contato com decisores de compras
          </p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          className="py-2.5 px-4 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded-xl shadow-md shadow-blue-500/20 flex items-center gap-2 transition-all cursor-pointer"
        >
          <Plus className="w-4 h-4" />
          <span>Novo Contato</span>
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
            placeholder="Buscar por nome do contato..."
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
            <span className="text-sm font-medium">Carregando contatos...</span>
          </div>
        ) : contacts.length === 0 ? (
          <div className="p-12 text-center">
            <Contact2 className="w-12 h-12 text-slate-300 mx-auto mb-3" />
            <h3 className="text-base font-semibold text-slate-800">Nenhum contato encontrado</h3>
            <p className="text-xs text-slate-500 mt-1 max-w-sm mx-auto">
              Cadastre pessoas chave ou converta leads comerciais diretamente.
            </p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50/80 text-xs font-semibold text-slate-500 uppercase border-b border-slate-200/80">
                <tr>
                  <th className="px-6 py-4">Nome & Cargo</th>
                  <th className="px-6 py-4">Empresa</th>
                  <th className="px-6 py-4">E-mail</th>
                  <th className="px-6 py-4">Telefone</th>
                  <th className="px-6 py-4">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {contacts.map((contact) => (
                  <tr key={contact.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-semibold text-slate-900">{contact.name}</div>
                      {contact.jobTitle && (
                        <div className="text-xs text-slate-500 flex items-center gap-1 mt-0.5">
                          <Briefcase className="w-3 h-3 text-slate-400" />
                          {contact.jobTitle}
                        </div>
                      )}
                    </td>

                    <td className="px-6 py-4">
                      {contact.companyName ? (
                        <span className="flex items-center gap-1.5 font-medium text-slate-800">
                          <Building className="w-3.5 h-3.5 text-slate-400" />
                          {contact.companyName}
                        </span>
                      ) : (
                        <span className="text-xs text-slate-400 italic">Pessoa Física / Avulso</span>
                      )}
                    </td>

                    <td className="px-6 py-4">
                      {contact.email ? (
                        <span className="flex items-center gap-1.5 text-xs text-slate-600">
                          <Mail className="w-3.5 h-3.5 text-slate-400" />
                          {contact.email}
                        </span>
                      ) : (
                        <span className="text-xs text-slate-400">-</span>
                      )}
                    </td>

                    <td className="px-6 py-4">
                      {contact.phone ? (
                        <span className="flex items-center gap-1.5 text-xs text-slate-600">
                          <Phone className="w-3.5 h-3.5 text-slate-400" />
                          {contact.phone}
                        </span>
                      ) : (
                        <span className="text-xs text-slate-400">-</span>
                      )}
                    </td>

                    <td className="px-6 py-4">
                      <span className="px-2.5 py-0.5 text-xs font-semibold rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200">
                        {contact.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* New Contact Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-100">
            <div className="flex justify-between items-center mb-5 pb-3 border-b border-slate-100">
              <h3 className="text-lg font-bold text-slate-900">Novo Contato</h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 p-1"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateContact} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  Nome Completo *
                </label>
                <input
                  required
                  placeholder="Ex: Ana Paula Ribeiro"
                  value={newContact.name}
                  onChange={(e) => setNewContact({ ...newContact, name: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  Empresa Vinculada
                </label>
                <select
                  value={newContact.companyId}
                  onChange={(e) => setNewContact({ ...newContact, companyId: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-800"
                >
                  <option value="">Nenhuma / Sem Empresa</option>
                  {companies.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.name}
                    </option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Cargo / Função
                  </label>
                  <input
                    placeholder="Ex: Gerente de Compras"
                    value={newContact.jobTitle}
                    onChange={(e) => setNewContact({ ...newContact, jobTitle: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Telefone
                  </label>
                  <input
                    placeholder="(11) 97777-6666"
                    value={newContact.phone}
                    onChange={(e) => setNewContact({ ...newContact, phone: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  E-mail
                </label>
                <input
                  type="email"
                  placeholder="ana@empresa.com"
                  value={newContact.email}
                  onChange={(e) => setNewContact({ ...newContact, email: e.target.value })}
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
                  Salvar Contato
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
