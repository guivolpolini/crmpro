import React, { useState } from 'react'
import { api } from '@/lib/api'
import { AiPitchResponse } from '@/types/ai'
import {
  Sparkles,
  Send,
  Copy,
  Check,
  RefreshCw,
  Lightbulb,
  MessageSquareText,
  Target,
  FileCheck
} from 'lucide-react'

export const AiAssistantPage: React.FC = () => {
  const [recipientName, setRecipientName] = useState('')
  const [segment, setSegment] = useState('')
  const [painPoints, setPainPoints] = useState('')
  const [targetProductName, setTargetProductName] = useState('')
  const [loading, setLoading] = useState(false)
  const [pitch, setPitch] = useState<AiPitchResponse | null>(null)
  const [copied, setCopied] = useState(false)

  const handleGenerate = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!recipientName.trim()) return

    setLoading(true)
    try {
      const res = await api.post('/ai/pitch', {
        recipientName,
        segment: segment || undefined,
        painPoints: painPoints || undefined,
        targetProductName: targetProductName || undefined
      })
      setPitch(res.data.data)
      setCopied(false)
    } catch (err) {
      console.error('Falha ao gerar pitch de IA', err)
    } finally {
      setLoading(false)
    }
  }

  const handleCopy = () => {
    if (!pitch) return
    const textToCopy = `Assunto: ${pitch.subject}\n\n${pitch.pitchText}\n\n${pitch.callToAction}`
    navigator.clipboard.writeText(textToCopy)
    setCopied(true)
    setTimeout(() => setCopied(false), 2500)
  }

  return (
    <div className="space-y-8 max-w-6xl mx-auto pb-10">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-gradient-to-r from-indigo-950 via-purple-950 to-slate-900 p-8 rounded-3xl text-white border border-indigo-900 shadow-xl">
        <div>
          <div className="flex items-center gap-2 mb-2">
            <span className="flex items-center gap-1.5 text-xs font-bold px-3 py-1 rounded-full bg-purple-500/20 text-purple-300 border border-purple-400/30">
              <Sparkles className="w-3.5 h-3.5 text-purple-300" />
              IA Comercial FreeLLMAPI
            </span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
            Assistente Comercial Cognitivo
          </h1>
          <p className="text-sm text-slate-300 mt-2 max-w-2xl">
            Gere abordagens comerciais altamente personalizadas, quebre objeções e acelere a conversão de leads com inteligência artificial integrada.
          </p>
        </div>

        <div className="p-4 bg-white/10 backdrop-blur-md rounded-2xl border border-white/10 text-xs shrink-0 flex items-center gap-3">
          <Lightbulb className="w-6 h-6 text-amber-300" />
          <div>
            <p className="font-bold text-white">Fallback Automático</p>
            <p className="text-purple-200 text-[11px]">FreeLLMAPI (34 provedores) + Motor Nativo</p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        {/* Form Column */}
        <div className="lg:col-span-5 bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs">
          <h2 className="text-base font-bold text-slate-900 mb-4 flex items-center gap-2">
            <Target className="w-5 h-5 text-indigo-600" />
            Parâmetros do Prospect
          </h2>

          <form onSubmit={handleGenerate} className="space-y-4">
            <div>
              <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                Nome do Lead / Decisor *
              </label>
              <input
                type="text"
                required
                placeholder="Ex: Diretor Rodrigo Mendes"
                value={recipientName}
                onChange={(e) => setRecipientName(e.target.value)}
                className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm"
              />
            </div>

            <div>
              <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                Setor / Ramo de Atuação
              </label>
              <input
                type="text"
                placeholder="Ex: Logística, Saúde, Varejo, SaaS"
                value={segment}
                onChange={(e) => setSegment(e.target.value)}
                className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm"
              />
            </div>

            <div>
              <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                Dor / Necessidade Identificada
              </label>
              <textarea
                rows={3}
                placeholder="Ex: Dificuldade em acompanhar follow-ups e perda de negócios no funil"
                value={painPoints}
                onChange={(e) => setPainPoints(e.target.value)}
                className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm resize-none"
              />
            </div>

            <div>
              <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                Produto / Solução a Ofertar
              </label>
              <input
                type="text"
                placeholder="Ex: CRM PRO Enterprise ou Módulo Kanban"
                value={targetProductName}
                onChange={(e) => setTargetProductName(e.target.value)}
                className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm"
              />
            </div>

            <button
              type="submit"
              disabled={loading || !recipientName.trim()}
              className="w-full flex items-center justify-center gap-2 py-3 px-4 bg-indigo-600 hover:bg-indigo-700 active:bg-indigo-800 disabled:opacity-50 text-white rounded-xl text-sm font-semibold shadow-md shadow-indigo-600/20 transition-all mt-4"
            >
              {loading ? (
                <>
                  <RefreshCw className="w-4 h-4 animate-spin" />
                  Gerando Pitch com IA...
                </>
              ) : (
                <>
                  <Sparkles className="w-4 h-4" />
                  Gerar Pitch de Alta Conversão
                </>
              )}
            </button>
          </form>
        </div>

        {/* Output Column */}
        <div className="lg:col-span-7 bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between pb-4 border-b border-slate-100 mb-4">
              <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
                <MessageSquareText className="w-5 h-5 text-indigo-600" />
                Abordagem Comercial Gerada
              </h2>

              {pitch && (
                <button
                  onClick={handleCopy}
                  className="flex items-center gap-1.5 text-xs font-semibold px-3 py-1.5 rounded-lg border border-slate-200 hover:bg-slate-50 text-slate-700 transition-colors"
                >
                  {copied ? (
                    <>
                      <Check className="w-3.5 h-3.5 text-emerald-600" />
                      <span className="text-emerald-600">Copiado!</span>
                    </>
                  ) : (
                    <>
                      <Copy className="w-3.5 h-3.5" />
                      Copiar Mensagem
                    </>
                  )}
                </button>
              )}
            </div>

            {!pitch ? (
              <div className="h-72 flex flex-col items-center justify-center text-center text-slate-400 p-8 border-2 border-dashed border-slate-100 rounded-xl">
                <Sparkles className="w-10 h-10 text-slate-300 mb-3" />
                <p className="text-sm font-medium text-slate-600">Nenhum pitch gerado ainda</p>
                <p className="text-xs text-slate-400 max-w-sm mt-1">
                  Preencha os dados do cliente ao lado e clique em "Gerar Pitch de Alta Conversão" para obter uma mensagem sob medida.
                </p>
              </div>
            ) : (
              <div className="space-y-4">
                <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-100">
                  <span className="text-xs font-bold uppercase text-slate-500">Sugestão de Assunto:</span>
                  <p className="text-sm font-bold text-slate-900 mt-1">{pitch.subject}</p>
                </div>

                <div className="p-4 rounded-xl bg-slate-50/70 border border-slate-100">
                  <span className="text-xs font-bold uppercase text-slate-500 mb-2 block">
                    Corpo da Mensagem:
                  </span>
                  <p className="text-sm text-slate-800 whitespace-pre-line leading-relaxed">
                    {pitch.pitchText}
                  </p>
                </div>

                <div className="p-3.5 rounded-xl bg-indigo-50/50 border border-indigo-100/80">
                  <span className="text-xs font-bold uppercase text-indigo-700">Chamada para Ação (CTA):</span>
                  <p className="text-sm font-semibold text-indigo-900 mt-1">{pitch.callToAction}</p>
                </div>
              </div>
            )}
          </div>

          <div className="pt-4 border-t border-slate-100 mt-6 flex items-center justify-between text-xs text-slate-400">
            <span className="flex items-center gap-1">
              <FileCheck className="w-3.5 h-3.5 text-emerald-500" />
              Pronto para e-mail, WhatsApp ou LinkedIn InMail
            </span>
            <span>CRM PRO AI Engine</span>
          </div>
        </div>
      </div>
    </div>
  )
}
export default AiAssistantPage
