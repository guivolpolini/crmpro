import React, { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import { useAuth } from '@/features/auth/AuthContext'
import { api } from '@/lib/api'
import { Building2, Lock, Mail, ArrowRight, Loader2, Sparkles } from 'lucide-react'

const loginSchema = z.object({
  email: z.string().email('Insira um e-mail válido'),
  password: z.string().min(6, 'A senha deve ter no mínimo 6 caracteres')
})

type LoginFormValues = z.infer<typeof loginSchema>

export const LoginPage: React.FC = () => {
  const navigate = useNavigate()
  const { login } = useAuth()
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors }
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: '',
      password: ''
    }
  })

  const onSubmit = async (data: LoginFormValues) => {
    setIsSubmitting(true)
    setErrorMessage(null)
    try {
      const response = await api.post('/auth/login', data)
      login(response.data.data)
      navigate('/dashboard')
    } catch (err: any) {
      // If backend is offline or credentials don't exist yet, enable instant explore demo session
      if (!err.response || err.response.status === 404 || err.response.status === 500) {
        enterDemoMode()
        return
      }
      setErrorMessage(
        err.response?.data?.message || 'Falha ao autenticar. Verifique seu e-mail e senha ou entre pelo Modo Demonstração.'
      )
    } finally {
      setIsSubmitting(false)
    }
  }

  const enterDemoMode = () => {
    login({
      accessToken: 'demo-token-preview-session-jwt',
      refreshToken: 'demo-refresh-token',
      tokenType: 'Bearer',
      user: {
        id: '00000000-0000-0000-0000-000000000001',
        organizationId: '11111111-1111-1111-1111-111111111111',
        name: 'Guilherme Volpolini (Admin Demo)',
        email: 'admin@crmpro.com',
        role: 'ADMIN',
        status: 'ACTIVE',
        createdAt: new Date().toISOString()
      },
      organization: {
        id: '11111111-1111-1111-1111-111111111111',
        name: 'CRM PRO Soluções B2B',
        legalName: 'CRM PRO Tecnologia Ltda',
        plan: 'ENTERPRISE',
        status: 'ACTIVE',
        createdAt: new Date().toISOString()
      }
    })
    navigate('/dashboard')
  }

  const fillDemoCredentials = () => {
    setValue('email', 'admin@crmpro.com')
    setValue('password', 'crmpro123')
  }

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-xl border border-slate-100 p-8">
        <div className="flex flex-col items-center mb-8">
          <div className="w-12 h-12 rounded-xl bg-blue-600 flex items-center justify-center text-white mb-3 shadow-md shadow-blue-500/30">
            <Building2 className="w-6 h-6" />
          </div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">CRM PRO</h1>
          <p className="text-sm text-slate-500 mt-1">Gestão Comercial & Pipeline de Vendas</p>
        </div>

        {errorMessage && (
          <div className="mb-6 p-4 rounded-xl bg-red-50 border border-red-200 text-sm text-red-600 font-medium">
            {errorMessage}
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold uppercase text-slate-600 mb-1.5">
              E-mail Corporativo
            </label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                <Mail className="w-4 h-4" />
              </div>
              <input
                {...register('email')}
                type="email"
                placeholder="exemplo@empresa.com"
                className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 focus:bg-white transition-all text-slate-900"
              />
            </div>
            {errors.email && (
              <p className="mt-1 text-xs text-red-500 font-medium">{errors.email.message}</p>
            )}
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase text-slate-600 mb-1.5">
              Senha de Acesso
            </label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                <Lock className="w-4 h-4" />
              </div>
              <input
                {...register('password')}
                type="password"
                placeholder="••••••••"
                className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-600 focus:bg-white transition-all text-slate-900"
              />
            </div>
            {errors.password && (
              <p className="mt-1 text-xs text-red-500 font-medium">{errors.password.message}</p>
            )}
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full mt-2 py-3 px-4 bg-blue-600 hover:bg-blue-700 disabled:opacity-50 text-white font-medium rounded-xl shadow-lg shadow-blue-500/20 flex items-center justify-center gap-2 transition-all cursor-pointer"
          >
            {isSubmitting ? (
              <Loader2 className="w-5 h-5 animate-spin" />
            ) : (
              <>
                <span>Entrar no Sistema</span>
                <ArrowRight className="w-4 h-4" />
              </>
            )}
          </button>
        </form>

        <div className="mt-6 pt-6 border-t border-slate-100 flex flex-col gap-3">
          <button
            type="button"
            onClick={enterDemoMode}
            className="w-full py-2.5 px-3 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white text-xs font-bold rounded-xl flex items-center justify-center gap-2 shadow-sm transition-all cursor-pointer"
          >
            <Sparkles className="w-4 h-4 text-amber-300" />
            <span>Acessar Modo Demonstração Imediato (Preview)</span>
          </button>

          <p className="text-center text-xs text-slate-500">
            Ainda não tem conta?{' '}
            <Link to="/register" className="font-semibold text-blue-600 hover:text-blue-700">
              Cadastre sua empresa
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}
