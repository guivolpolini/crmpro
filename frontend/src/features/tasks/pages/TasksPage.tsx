import React, { useState, useEffect } from 'react'
import { api } from '@/lib/api'
import { Task } from '@/types/operations'
import { Deal } from '@/types/pipeline'
import { Contact } from '@/types/crm'
import {
  CheckSquare,
  Plus,
  Calendar,
  AlertCircle,
  Briefcase,
  User,
  CheckCircle2,
  Circle,
  Loader2,
  X
} from 'lucide-react'

export const TasksPage: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([])
  const [deals, setDeals] = useState<Deal[]>([])
  const [contacts, setContacts] = useState<Contact[]>([])
  const [loading, setLoading] = useState(true)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [togglingId, setTogglingId] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  const [newTask, setNewTask] = useState({
    title: '',
    description: '',
    priority: 'MEDIUM',
    dealId: '',
    contactId: '',
    dueDate: ''
  })

  const fetchTasks = async () => {
    setLoading(true)
    try {
      const [tasksRes, dealsRes, contactsRes] = await Promise.all([
        api.get('/tasks'),
        api.get('/deals/kanban'),
        api.get('/contacts?size=100')
      ])
      setTasks(tasksRes.data.data || [])
      setDeals(dealsRes.data.data || [])
      setContacts(contactsRes.data.data.content || [])
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchTasks()
  }, [])

  const handleCreateTask = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await api.post('/tasks', {
        ...newTask,
        dealId: newTask.dealId || null,
        contactId: newTask.contactId || null,
        dueDate: newTask.dueDate ? new Date(newTask.dueDate).toISOString() : null
      })
      setIsModalOpen(false)
      setNewTask({
        title: '',
        description: '',
        priority: 'MEDIUM',
        dealId: '',
        contactId: '',
        dueDate: ''
      })
      setSuccessMessage('Tarefa agendada com sucesso!')
      fetchTasks()
      setTimeout(() => setSuccessMessage(null), 4000)
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao criar tarefa')
    }
  }

  const handleToggle = async (taskId: string) => {
    setTogglingId(taskId)
    try {
      await api.patch(`/tasks/${taskId}/toggle`)
      fetchTasks()
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao alterar status da tarefa')
    } finally {
      setTogglingId(null)
    }
  }

  const getPriorityBadge = (priority: string) => {
    switch (priority) {
      case 'URGENT':
        return <span className="px-2 py-0.5 text-xs font-bold rounded-md bg-red-50 text-red-700 border border-red-200">Urgente</span>
      case 'HIGH':
        return <span className="px-2 py-0.5 text-xs font-semibold rounded-md bg-amber-50 text-amber-700 border border-amber-200">Alta</span>
      case 'LOW':
        return <span className="px-2 py-0.5 text-xs font-medium rounded-md bg-slate-100 text-slate-600">Baixa</span>
      default:
        return <span className="px-2 py-0.5 text-xs font-medium rounded-md bg-blue-50 text-blue-700 border border-blue-200">Média</span>
    }
  }

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <CheckSquare className="w-6 h-6 text-blue-600" />
            Tarefas & Prazos Comerciais
          </h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Organize follow-ups, lembretes de ligação e reuniões com decisores
          </p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          className="py-2.5 px-4 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded-xl shadow-md shadow-blue-500/20 flex items-center gap-2 transition-all cursor-pointer"
        >
          <Plus className="w-4 h-4" />
          <span>Nova Tarefa</span>
        </button>
      </div>

      {successMessage && (
        <div className="p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-sm font-medium flex items-center gap-2.5 shadow-xs">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
          <span>{successMessage}</span>
        </div>
      )}

      {/* Task List */}
      <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
        {loading ? (
          <div className="p-12 flex justify-center items-center text-slate-400 gap-2">
            <Loader2 className="w-6 h-6 animate-spin text-blue-600" />
            <span className="text-sm font-medium">Carregando tarefas...</span>
          </div>
        ) : tasks.length === 0 ? (
          <div className="p-12 text-center">
            <CheckSquare className="w-12 h-12 text-slate-300 mx-auto mb-3" />
            <h3 className="text-base font-semibold text-slate-800">Nenhuma tarefa pendente</h3>
            <p className="text-xs text-slate-500 mt-1">Crie tarefas de prospecção ou agende um follow-up.</p>
          </div>
        ) : (
          <div className="divide-y divide-slate-100">
            {tasks.map((task) => {
              const isCompleted = task.status === 'COMPLETED'
              return (
                <div
                  key={task.id}
                  className={`p-4 sm:p-5 flex items-start justify-between gap-4 transition-colors ${
                    isCompleted ? 'bg-slate-50/50' : 'hover:bg-slate-50/70'
                  }`}
                >
                  <div className="flex items-start gap-3.5 flex-1">
                    <button
                      onClick={() => handleToggle(task.id)}
                      disabled={togglingId === task.id}
                      className="mt-0.5 text-slate-400 hover:text-blue-600 transition-colors cursor-pointer disabled:opacity-50"
                    >
                      {togglingId === task.id ? (
                        <Loader2 className="w-5 h-5 animate-spin text-blue-600" />
                      ) : isCompleted ? (
                        <CheckCircle2 className="w-5 h-5 text-emerald-600" />
                      ) : (
                        <Circle className="w-5 h-5" />
                      )}
                    </button>

                    <div className="space-y-1">
                      <h4
                        className={`text-sm font-bold ${
                          isCompleted ? 'line-through text-slate-400' : 'text-slate-900'
                        }`}
                      >
                        {task.title}
                      </h4>

                      {task.description && (
                        <p className="text-xs text-slate-500 leading-relaxed">{task.description}</p>
                      )}

                      <div className="flex flex-wrap items-center gap-3 text-xs text-slate-400 pt-1">
                        {getPriorityBadge(task.priority)}

                        {task.dueDate && (
                          <span className="flex items-center gap-1 text-slate-600 font-medium">
                            <Calendar className="w-3.5 h-3.5 text-slate-400" />
                            {new Date(task.dueDate).toLocaleDateString('pt-BR')}
                          </span>
                        )}

                        {task.dealTitle && (
                          <span className="flex items-center gap-1 text-slate-600">
                            <Briefcase className="w-3.5 h-3.5 text-slate-400" />
                            {task.dealTitle}
                          </span>
                        )}

                        {task.contactName && (
                          <span className="flex items-center gap-1 text-slate-600">
                            <User className="w-3.5 h-3.5 text-slate-400" />
                            {task.contactName}
                          </span>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              )
            })}
          </div>
        )}
      </div>

      {/* New Task Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-100">
            <div className="flex justify-between items-center mb-5 pb-3 border-b border-slate-100">
              <h3 className="text-lg font-bold text-slate-900">Agendar Nova Tarefa</h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 p-1"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateTask} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  Título da Tarefa *
                </label>
                <input
                  required
                  placeholder="Ex: Ligar para decisor e apresentar proposta"
                  value={newTask.title}
                  onChange={(e) => setNewTask({ ...newTask, title: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                  Descrição / Notas
                </label>
                <textarea
                  rows={2}
                  placeholder="Instruções para o contato..."
                  value={newTask.description}
                  onChange={(e) => setNewTask({ ...newTask, description: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900 resize-none"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Prioridade
                  </label>
                  <select
                    value={newTask.priority}
                    onChange={(e) => setNewTask({ ...newTask, priority: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-800"
                  >
                    <option value="LOW">Baixa</option>
                    <option value="MEDIUM">Média</option>
                    <option value="HIGH">Alta</option>
                    <option value="URGENT">Urgente</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Data Limite
                  </label>
                  <input
                    type="date"
                    value={newTask.dueDate}
                    onChange={(e) => setNewTask({ ...newTask, dueDate: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-900"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Oportunidade (Deal)
                  </label>
                  <select
                    value={newTask.dealId}
                    onChange={(e) => setNewTask({ ...newTask, dealId: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-800"
                  >
                    <option value="">Nenhuma / Avulsa</option>
                    {deals.map((d) => (
                      <option key={d.id} value={d.id}>
                        {d.title}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold uppercase text-slate-600 mb-1">
                    Contato
                  </label>
                  <select
                    value={newTask.contactId}
                    onChange={(e) => setNewTask({ ...newTask, contactId: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:outline-none focus:ring-2 focus:ring-blue-600 text-slate-800"
                  >
                    <option value="">Nenhum</option>
                    {contacts.map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.name}
                      </option>
                    ))}
                  </select>
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
                  Criar Tarefa
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
