# 💼 CRM Pro

> Um CRM B2B multi-tenant, rápido e direto ao ponto — construído para organizar funis de vendas, fechar propostas e garantir isolamento estrito de dados entre empresas.

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.4](https://img.shields.io/badge/Spring%20Boot-3.4.3-green.svg)](https://spring.io/projects/spring-boot)
[![React 19](https://img.shields.io/badge/React-19-blue.svg)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-blue.svg)](https://www.typescriptlang.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED.svg)](https://www.docker.com/)

---

## 💡 Por que este projeto existe?

A maioria dos CRMs de mercado (HubSpot, Salesforce, Pipedrive) sofre de dois extremos: ou são **caros e inflados demais** para operações ágeis, ou são **simples planilhas disfarçadas** sem segurança transacional.

Construí o **CRM Pro** com o objetivo de ter um sistema comercial completo, limpo e com padrão de código de produção:
- **Segurança B2B real:** suporte nativo a múltiplas empresas (multi-tenant), sem risco de vazamento de dados.
- **Transações ACID:** converter um lead em cliente não pode deixar registros órfãos pelo caminho.
- **Interface fluida:** Kanban responsivo e sem lentidão para quem passa o dia movimentando negócios.

---

## ✨ O que ele resolve na prática

- **🏢 Multi-Tenancy Isolado:** Múltiplas organizações usam o mesmo banco, mas os dados são rigidamente segregados via contexto da requisição (`organization_id`). O que é da Empresa A, a Empresa B jamais enxerga.
- **⚡ Conversão Atômica de Lead:** Com um clique, um Lead qualificado vira simultaneamente **Empresa + Contato + Negócio no Funil** em uma única transação de banco. Se qualquer etapa falhar, o rollback é automático.
- **📊 Funil de Vendas Interativo (Kanban):** 6 estágios comerciais padronizados (*Prospecção, Qualificação, Apresentação, Proposta, Negociação e Fechamento*) com totalizadores de receita e cards arrastáveis.
- **📑 Catálogo & Gerador de Propostas:** Tabela de produtos/serviços e emissão de propostas comerciais com numeração sequencial auditável (`PROP-XXXXXX`).
- **📈 Dashboard Executivo:** Métricas reais de faturamento ganho, pipeline em aberto, taxas de conversão e tarefas pendentes.

---

## 🧠 Bastidores & Decisões de Arquitetura

### Back-end (Java 21 + Spring Boot 3.4)
- **Multi-Tenancy contextual:** Implementado com interceptor e `ThreadLocal` (`TenantContext`). Toda query ao PostgreSQL é filtrada pelo tenant logado, eliminando risco de esquecimento em cláusulas `WHERE`.
- **Versionamento de banco:** Migrações com **Flyway** (V1 a V4), permitindo recriar a estrutura exata do banco em qualquer ambiente sem scripts manuais.
- **Segurança & JWT:** Spring Security 6 com autenticação stateless HMAC-SHA256 e rotação de Refresh Token.

### Front-end (React 19 + TypeScript + Tailwind CSS v4)
- **Zero fricção de sessão:** Cliente Axios configurado com interceptors que capturam status `401` e renovam o token silenciosamente em segundo plano, sem chutar o usuário para fora da tela.
- **Gráficos com Recharts:** Visualização analítica do funil e distribuição percentual de vendas sem bibliotecas pesadas de terceiros.

---

## 🧪 Confiabilidade & Testes

Não adianta prometer isolamento de dados sem provar no código. O projeto inclui testes de integração automatizados rodando sobre banco H2:

- `MultiTenancySecurityTest`: valida que uma organização é bloqueada ao tentar consultar dados de outra.
- `LeadConversionIntegrationTest`: testa a atomicidade da conversão Lead ➔ Company + Contact + Deal.
- `DealPipelineIntegrationTest`: garante integridade dos valores ao movimentar cards entre etapas do funil.
- `TaskAndProposalIntegrationTest`: valida cálculos de propostas comerciais e checklists de tarefas.

---

## 🚀 Como rodar em 1 minuto

### Pré-requisitos
- [Docker](https://www.docker.com/) e Docker Compose instalados.

### Subir tudo (Banco + Back-end + Front-end)
```bash
docker compose up --build -d
