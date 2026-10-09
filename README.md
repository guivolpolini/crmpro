# CRM PRO — Sistema Completo de Gestão Comercial

[![CI/CD Pipeline](https://github.com/guivolpolini/crmpro/actions/workflows/ci.yml/badge.svg)](https://github.com/guivolpolini/crmpro/actions/workflows/ci.yml)
[![Java 21/24](https://img.shields.io/badge/Java-21%20%7C%2024-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.4](https://img.shields.io/badge/Spring%20Boot-3.4.3-green.svg)](https://spring.io/projects/spring-boot)
[![React 19](https://img.shields.io/badge/React-19-blue.svg)](https://react.dev/)
[![TypeScript 6](https://img.shields.io/badge/TypeScript-6.0-blue.svg)](https://www.typescriptlang.org/)
[![Tailwind CSS v4](https://img.shields.io/badge/TailwindCSS-v4-38bdf8.svg)](https://tailwindcss.com/)
[![PostgreSQL 16](https://img.shields.io/badge/PostgreSQL-16-336791.svg)](https://www.postgresql.org/)

CRM B2B moderno com arquitetura Multi-Tenant isolada, funil de vendas interativo tipo Kanban, conversão atômica de leads, catálogo de produtos com gerador de propostas comerciais e dashboard de analytics em tempo real.

---

## 🏛️ Arquitetura & Tecnologias

### Backend
- **Java 21 / 24** com **Spring Boot 3.4.3**
- **Spring Data JPA & Hibernate 6**
- **PostgreSQL 16** com versionamento atômico via **Flyway Migrations** (V1 a V4)
- **Spring Security 6** + **JWT (JJWT 0.12.6)** com HMAC-SHA256 e rotação de Refresh Token
- **Multi-Tenancy por Coluna Discriminadora**: `organization_id NOT NULL` em todas as tabelas comerciais com resolução contextual via `TenantContext` (ThreadLocal).
- **Testes Automatizados**: Spring Boot Test com banco em memória H2 (9 testes cobrindo segurança, multi-tenancy, conversão atômica, pipeline e propostas).

### Frontend
- **React 19** + **TypeScript 6** com **Vite 8**
- **Tailwind CSS v4** + Design tokens e UI anti-slop
- **Lucide Icons** para consistência visual
- **Recharts 3** para gráficos executivos interativos
- **Axios** com interceptors para injeção automática de Bearer Token e auto-refresh em 401.

---

## 🚀 Funcionalidades Principais

1. **Multi-Tenancy & Segurança**:
   - Registro de nova Organização e Administrador em transação única.
   - Isolamento total de dados por tenant (`TenantContext.getTenantId()`).
   - Rotação de tokens e renovação sem deslogar o usuário.
2. **Gestão de Contas & Contatos**:
   - Cadastro de Empresas (CNPJ, razão social, setor, faturamento estimado).
   - Cadastro de Contatos vinculados a empresas com cargos e telefones.
3. **Leads & Conversão Atômica**:
   - Qualificação com status (`NEW`, `CONTACTED`, `QUALIFIED`, `CONVERTED`, `LOST`).
   - Endpoint `POST /api/v1/leads/{id}/convert`: cria Company + Contact + Deal e encerra o Lead em uma única transação ACID.
4. **Funil de Vendas (Kanban Interativo)**:
   - Provisão automática de 6 estágios padrão (*Prospecção, Qualificação, Apresentação, Proposta, Negociação, Fechamento*).
   - Arraste visual ou botões rápidos para transição de estágios.
   - Totalizadores de volume financeiro e quantidade de deals por coluna.
5. **Operações Comerciais & Catálogo**:
   - Checklist de Tarefas com toggle rápido de status (`PENDING` <-> `COMPLETED`).
   - Histórico de Atividades (chamadas, reuniões, notas).
   - Catálogo de Produtos e Serviços.
   - Gerador de Propostas Comerciais com itens e geração de código serial sequencial (`PROP-XXXXXX`).
6. **Dashboard Executivo & Analytics**:
   - Cards KPI: Receita Ganha, Pipeline em Aberto, Taxa de Conversão de Leads e Tarefas Pendentes.
   - Gráfico de barras com volume financeiro em cada etapa do funil.
   - Gráfico de rosca com distribuição percentual de oportunidades.

---

## 📂 Estrutura do Repositório

```text
crmpro/
├── .github/workflows/
│   └── ci.yml                     # Pipeline GitHub Actions (Backend + Frontend)
├── backend/                       # API REST Spring Boot 3.4
│   ├── src/main/java/com/crmpro/
│   │   ├── auth/                  # JWT, Auth Controller & UserDetails
│   │   ├── company/               # Gestão de Empresas
│   │   ├── contact/               # Gestão de Contatos
│   │   ├── dashboard/             # Agregações analíticas e KPIs
│   │   ├── deal/                  # Pipeline, Estágios e Negócios (Kanban)
│   │   ├── lead/                  # Leads e conversão atômica
│   │   ├── organization/          # Entidade Tenant
│   │   ├── product/               # Catálogo de produtos
│   │   ├── proposal/              # Propostas comerciais
│   │   ├── security/              # Filtros JWT e TenantContext
│   │   ├── task/                  # Checklist de tarefas
│   │   └── user/                  # Usuários e papéis
│   └── src/main/resources/db/migration/ # Flyway SQL migrations (V1 a V4)
├── frontend/                      # Single Page Application React 19
│   ├── src/
│   │   ├── components/layout/     # AppLayout com sidebar e header
│   │   ├── features/              # Módulos verticais (auth, crm, pipeline, dashboard, etc.)
│   │   ├── lib/                   # Cliente HTTP Axios configurado
│   │   └── types/                 # Interfaces TypeScript tipadas
├── docker-compose.yml             # Postgres 16 pronto para uso
└── README.md
```

---

## 🛠️ Como Executar Localmente

### Pré-requisitos
- **Java 21** ou superior
- **Node.js 20+** e **npm**
- **Docker** e **Docker Compose** (opcional, para rodar PostgreSQL)

### 1. Iniciar Banco de Dados
```bash
docker compose up -d
```
O PostgreSQL estará disponível em `localhost:5432` com usuário `crmpro` e senha `crmpro_secret`.

### 2. Iniciar API Backend
```bash
cd backend
./mvnw spring-boot:run
```
A API iniciará na porta `8080`.
As migrações do banco serão aplicadas automaticamente pelo Flyway.

Para rodar a suíte completa de 9 testes automatizados:
```bash
./mvnw test
```

### 3. Iniciar Frontend
```bash
cd frontend
npm install
npm run dev
```
Acesse a aplicação em `http://localhost:5173`.

---

## 🧪 Testes Automatizados

A suíte de testes backend valida todos os fluxos críticos:
- `MultiTenancySecurityTest`: garante que uma organização nunca acessa dados de outra.
- `LeadConversionIntegrationTest`: valida a conversão transacional atômica de Lead -> Company/Contact/Deal.
- `DealPipelineIntegrationTest`: valida criação de estágios, deals e movimentação no funil.
- `TaskAndProposalIntegrationTest`: valida toggle de tarefas, catálogo de produtos e propostas comerciais.
- `DashboardAnalyticsIntegrationTest`: valida cálculos de receita ganha, pipeline aberto e taxas de conversão.

---

## 📄 Licença
Distribuído sob licença MIT. Desenvolvido por [Guilherme Volpolini](https://github.com/guivolpolini).
