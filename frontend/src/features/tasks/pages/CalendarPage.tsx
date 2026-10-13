import React, { useState, useEffect } from 'react'
import { api } from '@/lib/api'
import { Task } from '@/types/operations'
import {
  Calendar as CalendarIcon,
  ChevronLeft,
  ChevronRight,
  Clock,
  CheckCircle2,
  AlertCircle,
  Briefcase
} from 'lucide-react'

export const CalendarPage: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([])
  const [loading, setLoading] = useState(true)
  const [currentDate, setCurrentDate] = useState(new Date())

  const fetchTasks = async () => {
    setLoading(true)
    try {
      const res = await api.get('/tasks')
      setTasks(res.data.data || [])
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchTasks()
  }, [])

  const handleToggleTask = async (taskId: string) => {
    try {
      await api.patch(`/tasks/${taskId}/complete`)
      fetchTasks()
    } catch (err) {
      console.error(err)
    }
  }

  // Obter dias do mês atual
  const year = currentDate.getFullYear()
  const month = currentDate.getMonth()

  const firstDayOfMonth = new Date(year, month, 1)
  const lastDayOfMonth = new Date(year, month + 1, 0)
  const startingDayOfWeek = firstDayOfMonth.getDay() // 0 = Domingo
  const daysInMonth = lastDayOfMonth.getDate()

  const prevMonth = () => {
    setCurrentDate(new Date(year, month - 1, 1))
  }

  const nextMonth = () => {
    setCurrentDate(new Date(year, month + 1, 1))
  }

  const monthNames = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
  ]

  const dayNames = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb']

  // Mapear tarefas por dia (YYYY-MM-DD)
  const tasksByDay: Record<string, Task[]> = {}
  tasks.forEach((task) => {
    if (task.dueDate) {
      const dayKey = task.dueDate.split('T')[0]
      if (!tasksByDay[dayKey]) tasksByDay[dayKey] = []
      tasksByDay[dayKey].push(task)
    }
  })

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <CalendarIcon className="w-6 h-6 text-blue-600" />
            Agenda & Calendário de Atividades
          </h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Visualize prazos de follow-ups comerciais, reuniões e compromissos
          </p>
        </div>

        <div className="flex items-center gap-3 bg-white border border-slate-200/80 px-3 py-1.5 rounded-xl shadow-xs">
          <button
            onClick={prevMonth}
            className="p-1 hover:bg-slate-100 rounded-lg text-slate-600 transition-colors cursor-pointer"
          >
            <ChevronLeft className="w-5 h-5" />
          </button>
          <span className="font-bold text-sm text-slate-800 min-w-[130px] text-center">
            {monthNames[month]} {year}
          </span>
          <button
            onClick={nextMonth}
            className="p-1 hover:bg-slate-100 rounded-lg text-slate-600 transition-colors cursor-pointer"
          >
            <ChevronRight className="w-5 h-5" />
          </button>
        </div>
      </div>

      {/* Calendar Grid Container */}
      <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden">
        {/* Day Header */}
        <div className="grid grid-cols-7 border-b border-slate-200/80 bg-slate-50/70 text-center py-3">
          {dayNames.map((d, i) => (
            <div key={d} className={`text-xs font-bold uppercase tracking-wider ${i === 0 || i === 6 ? 'text-slate-400' : 'text-slate-700'}`}>
              {d}
            </div>
          ))}
        </div>

        {/* Days cells */}
        <div className="grid grid-cols-7 auto-rows-fr divide-x divide-y divide-slate-100 min-h-[500px]">
          {/* Células em branco antes do dia 1 */}
          {Array.from({ length: startingDayOfWeek }).map((_, i) => (
            <div key={`empty-${i}`} className="bg-slate-50/30 p-2 min-h-[100px]" />
          ))}

          {/* Dias do mês */}
          {Array.from({ length: daysInMonth }).map((_, i) => {
            const dayNumber = i + 1
            const monthStr = String(month + 1).padStart(2, '0')
            const dayStr = String(dayNumber).padStart(2, '0')
            const dateKey = `${year}-${monthStr}-${dayStr}`
            const dayTasks = tasksByDay[dateKey] || []

            const isToday =
              new Date().getFullYear() === year &&
              new Date().getMonth() === month &&
              new Date().getDate() === dayNumber

            return (
              <div
                key={dateKey}
                className={`p-2.5 min-h-[110px] flex flex-col justify-between transition-colors ${
                  isToday ? 'bg-blue-50/40' : 'hover:bg-slate-50/60'
                }`}
              >
                <div className="flex items-center justify-between mb-1.5">
                  <span
                    className={`text-xs font-bold w-6 h-6 flex items-center justify-center rounded-full ${
                      isToday
                        ? 'bg-blue-600 text-white shadow-xs'
                        : 'text-slate-700'
                    }`}
                  >
                    {dayNumber}
                  </span>
                  {dayTasks.length > 0 && (
                    <span className="text-[10px] font-semibold text-slate-400">
                      {dayTasks.length} {dayTasks.length === 1 ? 'item' : 'itens'}
                    </span>
                  )}
                </div>

                <div className="space-y-1.5 flex-1 overflow-y-auto max-h-[90px]">
                  {dayTasks.map((t) => (
                    <div
                      key={t.id}
                      onClick={() => handleToggleTask(t.id)}
                      title={`Clique para alternar conclusão. Prioridade: ${t.priority}`}
                      className={`text-[11px] p-1.5 rounded-lg border font-medium cursor-pointer transition-all flex items-center justify-between gap-1 shadow-2xs ${
                        t.status === 'COMPLETED'
                          ? 'bg-slate-100 text-slate-400 border-slate-200 line-through'
                          : t.priority === 'HIGH'
                          ? 'bg-rose-50 text-rose-800 border-rose-200 hover:bg-rose-100'
                          : 'bg-blue-50 text-blue-800 border-blue-200 hover:bg-blue-100'
                      }`}
                    >
                      <span className="truncate">{t.title}</span>
                      {t.status === 'COMPLETED' ? (
                        <CheckCircle2 className="w-3 h-3 text-emerald-600 shrink-0" />
                      ) : (
                        <Clock className="w-3 h-3 text-slate-400 shrink-0" />
                      )}
                    </div>
                  ))}
                </div>
              </div>
            )
          })}
        </div>
      </div>
    </div>
  )
}
