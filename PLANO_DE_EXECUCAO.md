# Plano de Execução: CRM PRO — Sistema Completo de Gestão Comercial

> **Objetivo**: Desenvolver uma solução comercial B2B completa com Backend Java 21 / Spring Boot 3.4 e Frontend React 19 / TypeScript / Tailwind CSS / shadcn/ui, multi-tenancy rigoroso, pipeline Kanban interativo, métricas de vendas e testes automatizados para portfólio profissional de alto nível.

---

## 1. Arquitetura da Solução e Divisão por Subagentes

### Matriz de Papéis e Subagentes (Aileron Orchestration)
1. **ORCHESTRATOR (Principal)**: Coordenação do ciclo de vida, gates de aprovação e sincronização entre módulos.
2. **ARCHITECT**: Estruturação do repositório, contratos de OpenAPI (`openapi.yaml`), padronização de DTOs, erros e respostas da API.
3. **DATABASE**: Modelagem relacional 100% blindada com `organization_id NOT NULL`, migrations Flyway `V1__...`, índices e seeds consistentes.
4. **BACKEND**: Implementação dos módulos de negócio Spring Boot, Spring Security (JWT com stateless session), JPA com especificação de tenancy seguro, services e mappers.
5. **FRONTEND**: SPA React + Vite + TypeScript, shadcn/ui, TanStack Query, React Hook Form + Zod, Recharts e Kanban drag-and-drop.
6. **TESTER**: Testes unitários (JUnit 5, Mockito), testes de integração com isolamento multi-tenant real e validações de API.
7. **SECURITY & CODE_REVIEWER**: Auditoria OWASP, validação de inputs, sanitização e conformidade com clean-code.

---

## 2. Fases de Execução & Entregáveis

### Fase 0: Setup de Repositório e Scaffolding
- Inicializar monorepo: `backend/`, `frontend/`, `docker/`, `.docs/`.
- Configurar Maven Wrapper (`mvnw` e `mvnw.cmd`) compatível com Java 21/24.
- Gerar `pom.xml` com dependências estáveis: Spring Boot 3.4, JPA, Security, Flyway, PostgreSQL, Validation, Lombok, OpenAPI.
- Inicializar frontend com Vite + React + TypeScript + Tailwind CSS + Lucide + shadcn/ui.
- Criar `docker-compose.yml` para PostgreSQL 16 e pgAdmin.
- **Evidência**: Backend compila via `./mvnw clean compile` e frontend via `npm run build`.

### Fase 1: Fundação Multi-tenant, Segurança & Autenticação
- Migrations Flyway `V1__init_auth_and_organizations.sql`.
- `SecurityConfig`, `JwtAuthenticationFilter`, `TokenService`.
- Contexto de organização: extração automática de `tenantId` do token JWT para `SecurityContextHolder`.
- Endpoints: registro de organização, login com JWT/refresh, `me`.
- Telas Frontend: Login, Cadastro de Empresa, Layout Authenticated com sidebar expansível.

### Fase 2: Gestão de Contatos, Empresas e Leads
- Migrations Flyway `V2__crm_entities.sql`.
- CRUDs completos: Empresas, Contatos vinculados, Leads qualificados.
- Conversão transacional atômica: Lead -> Contato + Empresa + Negócio.
- Telas Frontend: Tabelas com busca/paginação, modais com React Hook Form + Zod.

### Fase 3: Funil de Vendas (Kanban), Negócios e Oportunidades
- Migrations Flyway `V3__sales_pipeline_and_deals.sql`.
- Backend: validação de transição de estágios, cálculo de valor ponderado e perdas.
- Frontend: Quadro Kanban visual com Drag and Drop interativo, cartões de oportunidade ricos.

### Fase 4: Tarefas, Histórico de Atividades e Propostas Comerciais
- Migrations Flyway `V4__activities_and_proposals.sql`.
- Timeline de interações (chamadas, e-mails, notas, reuniões).
- Tarefas com prazo e prioridades.
- Propostas comerciais com produtos/itens, descontos e totalizadores.

### Fase 5: Dashboard Executivo, Relatórios e Métricas
- Endpoints agregados (vendas no mês, taxa de conversão, ticket médio, funil).
- Interface com gráficos Recharts e exportação CSV.

### Fase 6: Quality Assurance, Hardening, CI/CD e Documentação
- Bateria de testes automatizados (`Spring Boot Test`, `MockMvc`, multi-tenancy).
- OpenAPI / Swagger `/swagger-ui/index.html`.
- GitHub Actions CI (`.github/workflows/ci.yml`).
- `README.md` detalhado para portfólio e entrevistas.
