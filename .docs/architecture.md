# Arquitetura Técnica — CRM PRO

## 1. Multi-Tenancy & Segurança
O CRM PRO adota isolamento lógico rígido baseado no campo `organization_id` presente em todas as tabelas comerciais.

### Fluxo de Autenticação e Tenancy
1. **Registro**: `POST /api/v1/auth/register-company` cria um registro atômico na tabela `organizations` e o primeiro usuário `ADMIN` em `users`.
2. **Login**: `POST /api/v1/auth/login` valida credenciais com BCrypt e emite um Access Token JWT com claim `orgId`.
3. **Filtro de Segurança**: Em cada requisição HTTP, `JwtAuthenticationFilter` extrai o `orgId` do token e o define no `TenantContext` (`ThreadLocal<UUID>`).
4. **Isolamento de Entidades**: Classes que herdam de `TenantEntity` atribuem automaticamente o `organizationId` a partir do `TenantContext` no `@PrePersist`.
5. **Quality Gate**: O teste `MultiTenancySecurityTest` garante que usuários de diferentes empresas não consigam consultar nem manipular dados alheios.

## 2. Tecnologias Utilizadas
- **Backend**: Java 21, Spring Boot 3.4.3, Spring Data JPA, Spring Security, Flyway, PostgreSQL 16, JJWT 0.12.6, Springdoc OpenAPI 2.8.5.
- **Frontend**: React 19, Vite 8, TypeScript, Tailwind CSS v4, React Router 7, TanStack Query, React Hook Form, Zod, Lucide React.
