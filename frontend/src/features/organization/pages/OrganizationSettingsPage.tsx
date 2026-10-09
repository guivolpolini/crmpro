import React, { useEffect, useState } from 'react'
import { api } from '@/lib/api'
import { useAuth } from '@/features/auth/AuthContext'
import { User, Organization } from '@/types/auth'
import {
  Building2,
  Users,
  ShieldCheck,
  UserPlus,
  Trash2,
  Save,
  CheckCircle2,
  Mail,
  Phone,
  MapPin,
  FileText,
  BadgeCheck,
  RefreshCw,
  AlertCircle
} from 'lucide-react'

export const OrganizationSettingsPage: React.FC = () => {
  const { user: currentUser } = useAuth()
  const [org, setOrg] = useState<Organization | null>(null)
  const [team, setTeam] = useState<User[]>([])
  const [loading, setLoading] = useState(true)
  const [savingOrg, setSavingOrg] = useState(false)
  const [showInviteModal, setShowInviteModal] = useState(false)
  const [message, setMessage] = useState<{ text: string; type: 'success' | 'error' } | null>(null)

  // Form states for Organization
  const [orgForm, setOrgForm] = useState({
    name: '',
    legalName: '',
    document: '',
    email: '',
    phone: '',
    address: ''
  })

  // Form states for New Member
  const [newMember, setNewMember] = useState({
    name: '',
    email: '',
    password: '',
    role: 'SELLER' as 'ADMIN' | 'MANAGER' | 'SELLER'
  })
  const [addingMember, setAddingMember] = useState(false)

  const fetchData = async () => {
    setLoading(true)
    try {
      const [orgRes, teamRes] = await Promise.all([
        api.get('/organization'),
        api.get('/organization/users')
      ])
      const orgData = orgRes.data.data
      setOrg(orgData)
      setOrgForm({
        name: orgData.name || '',
        legalName: orgData.legalName || '',
        document: orgData.document || '',
        email: orgData.email || '',
        phone: orgData.phone || '',
        address: orgData.address || ''
      })
      setTeam(teamRes.data.data)
    } catch (err) {
      console.error('Falha ao carregar dados da organização', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData()
  }, [])

  const handleUpdateOrg = async (e: React.FormEvent) => {
    e.preventDefault()
    setSavingOrg(true)
    setMessage(null)
    try {
      const res = await api.put('/organization', orgForm)
      setOrg(res.data.data)
      setMessage({ text: 'Informações da empresa salvas com sucesso!', type: 'success' })
      setTimeout(() => setMessage(null), 3000)
    } catch (err: any) {
      setMessage({
        text: err.response?.data?.message || 'Erro ao atualizar dados corporativos',
        type: 'error'
      })
    } finally {
      setSavingOrg(false)
    }
  }

  const handleAddMember = async (e: React.FormEvent) => {
    e.preventDefault()
    setAddingMember(true)
    try {
      await api.post('/organization/users', newMember)
      setShowInviteModal(false)
      setNewMember({ name: '', email: '', password: '', role: 'SELLER' })
      fetchData()
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao adicionar colaborador')
    } finally {
      setAddingMember(false)
    }
  }

  const handleRemoveMember = async (id: string) => {
    if (id === currentUser?.id) {
      alert('Você não pode remover seu próprio usuário administrador.')
      return
    }
    if (!confirm('Deseja realmente remover este colaborador da organização?')) return

    try {
      await api.delete(`/organization/users/${id}`)
      setTeam(team.filter((u) => u.id !== id))
    } catch (err) {
      alert('Falha ao remover colaborador')
    }
  }

  return (
    <div className="space-y-8 max-w-6xl mx-auto pb-10">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-slate-900">
            Organização & Gestão de Equipe
          </h1>
          <p className="text-sm text-slate-500 mt-1">
            Configure dados cadastrais da empresa, plano de assinatura e membros comerciais
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={async () => {
              if (!confirm('Deseja popular a organização com empresas, contatos, leads e negócios de demonstração?')) return
              setLoading(true)
              try {
                await api.post('/demo/seed')
                setMessage({ text: 'Dados de demonstração gerados com sucesso!', type: 'success' })
                fetchData()
              } catch (err: any) {
                setMessage({ text: err.response?.data?.message || 'Falha ao gerar dados demo', type: 'error' })
              } finally {
                setLoading(false)
              }
            }}
            disabled={loading}
            className="flex items-center gap-1.5 px-3.5 py-2 bg-gradient-to-r from-amber-500 to-amber-600 hover:from-amber-600 hover:to-amber-700 text-white rounded-xl text-xs font-semibold shadow-xs transition-colors"
          >
            <BadgeCheck className="w-3.5 h-3.5" />
            Gerar Dados de Demonstração
          </button>

          <button
            onClick={fetchData}
            disabled={loading}
            className="flex items-center gap-2 px-3.5 py-2 bg-white border border-slate-200 hover:bg-slate-50 rounded-xl text-xs font-semibold text-slate-700 shadow-xs"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
            Atualizar
          </button>
        </div>
      </div>

      {message && (
        <div
          className={`p-4 rounded-xl text-sm font-semibold flex items-center gap-2 ${
            message.type === 'success'
              ? 'bg-emerald-50 text-emerald-800 border border-emerald-200'
              : 'bg-rose-50 text-rose-800 border border-rose-200'
          }`}
        >
          {message.type === 'success' ? (
            <CheckCircle2 className="w-5 h-5 text-emerald-600" />
          ) : (
            <AlertCircle className="w-5 h-5 text-rose-600" />
          )}
          {message.text}
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        {/* Company Settings */}
        <div className="lg:col-span-7 bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs">
          <div className="flex items-center justify-between pb-4 border-b border-slate-100 mb-6">
            <div className="flex items-center gap-2.5">
              <Building2 className="w-5 h-5 text-blue-600" />
              <h2 className="text-base font-bold text-slate-900">Perfil da Empresa</h2>
            </div>
            <span className="text-xs px-2.5 py-1 bg-blue-50 text-blue-700 font-bold rounded-full border border-blue-200">
              Plano: {org?.plan || 'ENTERPRISE'}
            </span>
          </div>

          <form onSubmit={handleUpdateOrg} className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                  Nome Fantasia *
                </label>
                <input
                  type="text"
                  required
                  value={orgForm.name}
                  onChange={(e) => setOrgForm({ ...orgForm, name: e.target.value })}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                  Razão Social
                </label>
                <input
                  type="text"
                  value={orgForm.legalName}
                  onChange={(e) => setOrgForm({ ...orgForm, legalName: e.target.value })}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                  CNPJ / Documento
                </label>
                <input
                  type="text"
                  placeholder="00.000.000/0001-00"
                  value={orgForm.document}
                  onChange={(e) => setOrgForm({ ...orgForm, document: e.target.value })}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                  Telefone Corporativo
                </label>
                <input
                  type="text"
                  placeholder="(11) 90000-0000"
                  value={orgForm.phone}
                  onChange={(e) => setOrgForm({ ...orgForm, phone: e.target.value })}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                E-mail Institucional
              </label>
              <input
                type="email"
                placeholder="contato@empresa.com"
                value={orgForm.email}
                onChange={(e) => setOrgForm({ ...orgForm, email: e.target.value })}
                className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
              />
            </div>

            <div>
              <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                Endereço Comercial
              </label>
              <input
                type="text"
                placeholder="Av. Paulista, 1000 - São Paulo, SP"
                value={orgForm.address}
                onChange={(e) => setOrgForm({ ...orgForm, address: e.target.value })}
                className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
              />
            </div>

            <div className="pt-2">
              <button
                type="submit"
                disabled={savingOrg}
                className="flex items-center justify-center gap-2 px-5 py-2.5 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white rounded-xl text-sm font-semibold shadow-sm transition-colors"
              >
                <Save className="w-4 h-4" />
                {savingOrg ? 'Salvando...' : 'Salvar Alterações'}
              </button>
            </div>
          </form>
        </div>

        {/* Team Members List */}
        <div className="lg:col-span-5 bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between pb-4 border-b border-slate-100 mb-4">
              <div className="flex items-center gap-2.5">
                <Users className="w-5 h-5 text-indigo-600" />
                <h2 className="text-base font-bold text-slate-900">Equipe de Vendas</h2>
              </div>

              <button
                onClick={() => setShowInviteModal(true)}
                className="flex items-center gap-1.5 text-xs font-semibold px-3 py-1.5 bg-indigo-50 text-indigo-700 hover:bg-indigo-100 rounded-lg border border-indigo-200 transition-colors"
              >
                <UserPlus className="w-3.5 h-3.5" />
                Adicionar Membro
              </button>
            </div>

            <div className="space-y-3">
              {team.map((member) => (
                <div
                  key={member.id}
                  className="p-3.5 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between"
                >
                  <div>
                    <div className="flex items-center gap-2">
                      <p className="text-xs font-bold text-slate-900">{member.name}</p>
                      <span
                        className={`text-[10px] font-bold px-1.5 py-0.5 rounded ${
                          member.role === 'ADMIN'
                            ? 'bg-purple-100 text-purple-700'
                            : 'bg-blue-100 text-blue-700'
                        }`}
                      >
                        {member.role}
                      </span>
                    </div>
                    <p className="text-[11px] text-slate-500 mt-0.5">{member.email}</p>
                  </div>

                  {member.id !== currentUser?.id && (
                    <button
                      onClick={() => handleRemoveMember(member.id)}
                      className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
                      title="Remover da equipe"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>

          <div className="pt-4 border-t border-slate-100 mt-6 text-xs text-slate-400 flex items-center gap-1.5">
            <ShieldCheck className="w-4 h-4 text-emerald-500" />
            Isolamento de dados restrito ao Tenant ID
          </div>
        </div>
      </div>

      {/* Invite Member Modal */}
      {showInviteModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 backdrop-blur-xs p-4">
          <div className="bg-white rounded-2xl p-6 max-w-md w-full shadow-2xl border border-slate-200">
            <h3 className="text-lg font-bold text-slate-900 mb-4">Adicionar Colaborador</h3>
            <form onSubmit={handleAddMember} className="space-y-3.5">
              <div>
                <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                  Nome Completo *
                </label>
                <input
                  type="text"
                  required
                  placeholder="Ex: Carlos Silva"
                  value={newMember.name}
                  onChange={(e) => setNewMember({ ...newMember, name: e.target.value })}
                  className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div>
                <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                  E-mail de Acesso *
                </label>
                <input
                  type="email"
                  required
                  placeholder="carlos@empresa.com"
                  value={newMember.email}
                  onChange={(e) => setNewMember({ ...newMember, email: e.target.value })}
                  className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div>
                <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                  Senha Provisória *
                </label>
                <input
                  type="password"
                  required
                  placeholder="Mínimo 6 caracteres"
                  value={newMember.password}
                  onChange={(e) => setNewMember({ ...newMember, password: e.target.value })}
                  className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div>
                <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                  Função / Papel *
                </label>
                <select
                  value={newMember.role}
                  onChange={(e) => setNewMember({ ...newMember, role: e.target.value as any })}
                  className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-indigo-500"
                >
                  <option value="SELLER">Vendedor / Executivo de Vendas</option>
                  <option value="MANAGER">Gerente Comercial</option>
                  <option value="ADMIN">Administrador</option>
                </select>
              </div>

              <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setShowInviteModal(false)}
                  className="px-4 py-2 text-sm text-slate-600 hover:bg-slate-100 rounded-xl"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={addingMember}
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-sm font-semibold shadow-xs"
                >
                  {addingMember ? 'Adicionando...' : 'Adicionar Membro'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
export default OrganizationSettingsPage
