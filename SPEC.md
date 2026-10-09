# PROJETO: CRM PRO — SISTEMA COMPLETO DE GESTÃO COMERCIAL

## 1. PAPEL E OBJETIVO

Atue como um engenheiro de software sênior, arquiteto de sistemas, especialista em Java/Spring Boot, React, UX/UI, segurança de aplicações e SaaS B2B.

Desenvolva um CRM completo para pequenas empresas, com qualidade de software profissional, arquitetura organizada, interface moderna e foco em usabilidade.

O sistema deve permitir que pequenas empresas gerenciem clientes, leads, oportunidades comerciais, tarefas, interações, propostas e resultados de vendas em um único lugar.

Não crie apenas uma demonstração visual, landing page ou CRUD superficial. Desenvolva uma aplicação funcional, com frontend conectado a um backend real, persistência em banco de dados, autenticação, autorização, validações e testes automatizados.

O projeto será utilizado como portfólio profissional para vagas de estágio em desenvolvimento Java e poderá evoluir futuramente para um SaaS comercial.

Antes de implementar, analise a estrutura existente do projeto, identifique tecnologias e componentes já disponíveis e apresente um plano de execução. Preserve funcionalidades existentes que estejam corretas.

## 2. STACK TECNOLÓGICA OBRIGATÓRIA

### Backend
- Java 21.
- Spring Boot 3.x, utilizando uma versão estável e compatível.
- Maven para gerenciamento de dependências.
- Spring Web para API REST.
- Spring Data JPA e Hibernate para persistência.
- Spring Security para autenticação e autorização.
- Autenticação com JWT usando biblioteca mantida e configuração segura.
- Bean Validation para validações.
- PostgreSQL como banco de dados principal.
- Flyway para migrations versionadas.
- Spring Boot Actuator para health checks.
- OpenAPI/Swagger para documentação da API.
- JUnit 5, Mockito e Spring Boot Test para testes.
- Testcontainers para testes de integração com PostgreSQL.

### Frontend
- React com TypeScript.
- Vite.
- Tailwind CSS.
- shadcn/ui para componentes acessíveis e consistentes.
- React Router para navegação.
- TanStack Query para consultas, cache e sincronização com a API.
- React Hook Form com Zod para formulários e validação.
- Biblioteca de gráficos, como Recharts.
- Lucide para ícones.
- Biblioteca de calendário compatível com React para agenda e tarefas.

### Infraestrutura
- Docker e Docker Compose.
- Git e GitHub.
- GitHub Actions para integração contínua.
- Arquivo `.env.example` com as variáveis necessárias, sem credenciais reais.
- Configuração de ambientes de desenvolvimento e produção.
- Backend e frontend executáveis localmente.
- Preparação para deploy em serviços compatíveis com Java, React e PostgreSQL.

Não substitua Java/Spring Boot por Node.js, Express, Firebase ou Supabase como backend principal. O backend de negócio deve ser implementado em Java.

## 3. ARQUITETURA

Utilize um monólito modular, organizado por domínio, adequado para uma aplicação de pequeno ou médio porte.

Organize os módulos de negócio:
- authentication
- organizations
- users
- contacts
- companies
- leads
- pipelines
- deals
- activities
- tasks
- notes
- products
- proposals
- dashboard
- reports
- notifications
- audit

Cada módulo deve separar adequadamente suas responsabilidades, evitando controllers com regras de negócio, entidades expostas diretamente na API e dependências circulares.

Estrutura sugerida do backend:

backend/src/main/java/com/crmpro/
- config/
- security/
- common/
  - exception/
  - validation/
  - response/
  - pagination/
- auth/
- organization/
- user/
- contact/
- company/
- lead/
- pipeline/
- deal/
- activity/
- task/
- note/
- product/
- proposal/
- dashboard/
- report/
- notification/
- audit/

Dentro de cada módulo, utilize as camadas necessárias:
- controller/
- service/
- repository/
- entity/
- dto/
- mapper/

Não crie classes ou abstrações sem necessidade. Mantenha o código simples, legível e testável.

Estrutura sugerida do frontend:

frontend/src/
- app/
- components/
  - ui/
  - layout/
  - data-table/
  - forms/
  - feedback/
- features/
  - auth/
  - dashboard/
  - contacts/
  - companies/
  - leads/
  - deals/
  - pipeline/
  - tasks/
  - activities/
  - products/
  - proposals/
  - reports/
  - settings/
- hooks/
- lib/
- services/
- types/
- routes/

Mantenha os módulos do frontend independentes e reutilize componentes quando isso reduzir duplicação real.

## 4. MULTIEMPRESA E ISOLAMENTO DE DADOS

O CRM deve permitir que diferentes empresas utilizem a mesma aplicação sem acessar os dados umas das outras.

Implemente um modelo multiempresa desde o início.

Cada empresa deve possuir:
- Identificador único.
- Nome comercial.
- Razão social opcional.
- CNPJ opcional.
- E-mail e telefone.
- Endereço opcional.
- Logotipo opcional.
- Configurações básicas.
- Plano e situação da assinatura, preparados para futura integração de cobrança.

Usuários devem pertencer a uma organização e ter um perfil de acesso.

Implemente isolamento de dados no backend, não apenas na interface.

Regras obrigatórias:
- Toda consulta de dados comerciais deve considerar a organização autenticada.
- Nunca confiar em `organizationId` enviado pelo frontend como prova de autorização.
- Validar a organização de origem e destino em operações entre entidades.
- Impedir acesso cruzado por URLs, IDs previsíveis, filtros ou requisições manipuladas.
- Testar explicitamente o isolamento entre duas empresas diferentes.

O cadastro da organização e do primeiro administrador deve ser realizado de forma transacional e segura.

Não permita que qualquer usuário se torne administrador de outra organização por meio de alterações no corpo de uma requisição.

## 5. AUTENTICAÇÃO E USUÁRIOS

Implemente:
- Login por e-mail e senha.
- Cadastro inicial de empresa e administrador.
- Logout com invalidação ou estratégia adequada para encerramento da sessão.
- Recuperação de senha com token temporário, caso exista infraestrutura de envio de e-mail configurada.
- Alteração de senha.
- Perfil do usuário.
- Ativação e desativação de usuários.
- Proteção de rotas.
- Controle de tentativas de login e limitação de requisições sensíveis.

Perfis:
- OWNER: proprietário da organização, com controle administrativo.
- ADMIN: administrador operacional da organização.
- MANAGER: gerente comercial.
- SALES_REP: vendedor.

Permissões devem ser aplicadas no backend, com regras explícitas por operação e recurso.

Exemplos:
- OWNER gerencia a organização e seus administradores.
- ADMIN gerencia usuários e configurações autorizadas.
- MANAGER acompanha a equipe e os indicadores comerciais.
- SALES_REP gerencia os próprios contatos, leads, negócios e tarefas, respeitando as permissões configuradas.

Não armazene senhas em texto puro. Utilize um algoritmo seguro de hash de senha, como BCrypt.

Configure CORS de forma restrita e documentada. Nunca permita origens arbitrárias em produção.

Para JWT, utilize expiração adequada e uma estratégia segura de armazenamento dos tokens. Não persista tokens de autenticação em localStorage por conveniência. Considere cookies HttpOnly, Secure e SameSite, com proteção CSRF quando aplicável.

## 6. DASHBOARD PRINCIPAL

Crie um dashboard profissional, com informações úteis para decisões comerciais.

Indicadores:
- Total de contatos.
- Leads ativos.
- Oportunidades abertas.
- Negócios ganhos.
- Negócios perdidos.
- Valor total do pipeline.
- Valor ponderado do pipeline.
- Receita de negócios ganhos no período.
- Taxa de conversão.
- Tarefas pendentes e atrasadas.
- Atividades comerciais recentes.

Gráficos:
- Evolução de negócios ganhos por período.
- Negócios por etapa do funil.
- Distribuição por origem dos leads.
- Desempenho comercial por vendedor, conforme permissões.
- Motivos de perda de negócios.
- Comparativo de resultados mensais.

Filtros:
- Hoje.
- Últimos 7 dias.
- Últimos 30 dias.
- Este mês.
- Mês anterior.
- Intervalo personalizado.

Todos os números devem ser calculados com dados reais do banco de dados, com regras de negócio consistentes e consultas eficientes.

Defina claramente como cada indicador é calculado. Não invente resultados nem utilize números estáticos em produção.

## 7. GESTÃO DE CONTATOS E EMPRESAS

Implemente um cadastro completo de contatos.

Campos:
- Nome completo.
- E-mail.
- Telefone.
- WhatsApp.
- Cargo.
- Empresa vinculada.
- Origem do contato.
- Responsável comercial.
- Tags.
- Observações.
- Data de criação.
- Última interação.
- Situação do contato.

Permita:
- Criar, editar, visualizar e arquivar contatos.
- Pesquisar por nome, e-mail, telefone e empresa.
- Filtrar por responsável, tag, origem e situação.
- Importar contatos por CSV com pré-visualização, validação e relatório de erros.
- Exportar dados autorizados para CSV.
- Identificar possíveis duplicidades.
- Associar contatos a empresas e negócios.
- Consultar o histórico de atividades.

Para empresas, implemente:
- Nome comercial.
- Razão social opcional.
- CNPJ opcional.
- Segmento.
- Site.
- E-mail e telefone.
- Endereço.
- Responsável comercial.
- Observações.
- Lista de contatos associados.
- Lista de negócios associados.

Não exponha informações de outras organizações durante importações, exportações ou pesquisas.

## 8. GESTÃO DE LEADS

Implemente um módulo para acompanhar potenciais clientes antes da conversão.

Campos:
- Nome do lead.
- Empresa.
- E-mail e telefone.
- Origem.
- Interesse.
- Responsável.
- Status.
- Pontuação opcional.
- Observações.
- Data de entrada.

Status:
- Novo.
- Contatado.
- Qualificado.
- Desqualificado.
- Convertido.

Funcionalidades:
- Cadastro manual.
- Busca e filtros.
- Histórico de contatos.
- Atribuição a vendedores.
- Tags.
- Próxima tarefa recomendada.
- Registro do motivo de desqualificação.
- Conversão do lead em contato e oportunidade comercial.

A conversão deve ser transacional e evitar duplicações caso a operação seja repetida.

Ao converter, preserve o histórico e as relações relevantes do lead.

## 9. FUNIL DE VENDAS E NEGÓCIOS

Implemente um pipeline visual em formato Kanban, com arrastar e soltar entre etapas.

Etapas iniciais:
1. Novo negócio.
2. Primeiro contato.
3. Reunião agendada.
4. Proposta enviada.
5. Negociação.
6. Ganho.
7. Perdido.

Permita que administradores autorizados personalizem as etapas, respeitando regras de transição e mantendo os registros históricos.

Cada negócio deve conter:
- Título.
- Contato associado.
- Empresa associada.
- Responsável comercial.
- Valor estimado.
- Etapa atual.
- Probabilidade de fechamento.
- Data prevista de fechamento.
- Origem.
- Produtos ou serviços associados, quando aplicável.
- Observações.
- Motivo de perda.
- Data de criação e atualização.
- Data de ganho ou perda.

Funcionalidades:
- Criar e editar negócios.
- Mover cartões entre etapas.
- Filtrar por responsável, etapa e período.
- Registrar mudanças de etapa.
- Consultar histórico do negócio.
- Registrar valor e data de fechamento.
- Exigir motivo ao perder um negócio.
- Confirmar operações importantes.
- Exibir valor total e ponderado do pipeline.

O valor ponderado deve ser calculado como valor estimado multiplicado pela probabilidade de fechamento, considerando os filtros e as regras adotadas.

Negócios ganhos e perdidos devem possuir estados finais coerentes. Para reabrir um negócio, exija permissão e registre a operação.

Implemente persistência e validação no backend ao mover negócios. A interface Kanban não pode ser a única responsável por validar as operações.

## 10. ATIVIDADES, TAREFAS E AGENDA

Crie um módulo para organizar a rotina comercial.

Tipos de atividade:
- Ligação.
- E-mail.
- Reunião.
- Mensagem.
- Demonstração.
- Follow-up.
- Observação.

Cada atividade deve registrar:
- Tipo.
- Título.
- Descrição.
- Responsável.
- Contato, empresa ou negócio associado.
- Data e horário.
- Resultado.
- Data de criação.

Tarefas devem possuir:
- Título e descrição.
- Responsável.
- Prazo.
- Prioridade.
- Status.
- Relacionamento opcional com contato ou negócio.
- Data de conclusão.

Implemente:
- Lista de tarefas.
- Filtros por status, responsável e prazo.
- Visão de tarefas atrasadas.
- Calendário semanal e mensal.
- Conclusão de tarefas.
- Histórico de atividades.
- Lembretes dentro da aplicação.

Permita registrar atividades concluídas sem prazo futuro. Diferencie tarefas pendentes de atividades já realizadas.

## 11. HISTÓRICO E ANOTAÇÕES

Cada contato e negócio deve possuir uma linha do tempo.

Registre:
- Criação e atualização.
- Mudanças de responsável.
- Mudanças de etapa.
- Comentários.
- Ligações e reuniões.
- Tarefas concluídas.
- Ganhos e perdas.
- Alterações relevantes nos dados.

Cada evento deve registrar autor, horário e entidade relacionada.

Diferencie anotações manuais de eventos automáticos do sistema.

O histórico de auditoria deve ser protegido contra edição por usuários comuns. Não permita que a interface altere ou apague eventos históricos livremente.

## 12. PRODUTOS E PROPOSTAS COMERCIAIS

Implemente um catálogo simples de produtos e serviços.

Campos:
- Nome.
- Descrição.
- Código opcional.
- Preço.
- Situação ativa ou inativa.

Permita associar produtos a negócios, definindo quantidade, preço negociado e desconto autorizado.

Crie um módulo de propostas:
- Número identificador.
- Cliente.
- Negócio associado.
- Itens e quantidades.
- Descontos.
- Valor total.
- Data de emissão.
- Validade.
- Condições comerciais.
- Status: rascunho, enviada, aceita, recusada ou expirada.

Calcule os totais no backend. Valide descontos e preços com regras configuráveis.

Gere PDF com layout profissional, identificação da empresa, dados do cliente, itens, valores e condições comerciais.

Se a geração de PDF tornar o cronograma inviável, priorize o módulo de produtos e propostas em formato web e implemente o PDF na fase final.

Não implemente pagamentos reais nesta primeira versão.

## 13. RELATÓRIOS

Implemente relatórios com filtros de período e permissões adequadas.

Relatórios:
- Negócios ganhos e perdidos.
- Conversão por etapa.
- Resultados por vendedor.
- Origem dos leads.
- Valor de oportunidades abertas.
- Tarefas concluídas e atrasadas.
- Tempo entre criação e fechamento de negócios.
- Motivos de perda.

Permita exportação para CSV.

Todos os relatórios devem utilizar dados reais e respeitar o isolamento multiempresa e o escopo de acesso de cada usuário.

Documente fórmulas e limitações dos indicadores para evitar interpretações incorretas.

## 14. NOTIFICAÇÕES

Implemente notificações internas para:
- Nova tarefa atribuída.
- Tarefa próxima do vencimento.
- Tarefa atrasada.
- Negócio atribuído.
- Mudança importante em negócio acompanhado.
- Proposta próxima do vencimento.

Inclua:
- Lista de notificações.
- Indicador de não lidas.
- Marcar como lida.
- Link para o registro relacionado.

Evite notificações duplicadas. E-mail e WhatsApp podem ficar para versões futuras.

## 15. EXPERIÊNCIA VISUAL E DESIGN

A interface deve parecer um produto SaaS profissional, não um template genérico gerado por IA.

Direção visual:
- Design limpo, sofisticado e orientado à produtividade.
- Sidebar recolhível.
- Cabeçalho com busca, notificações e perfil.
- Tipografia legível.
- Espaçamento consistente.
- Hierarquia visual clara.
- Componentes reutilizáveis.
- Ícones coerentes.
- Bordas e sombras discretas.
- Transições suaves.
- Suporte a telas desktop, tablet e celular.
- Estados de carregamento, erro, vazio e sucesso.
- Confirmações para operações destrutivas.
- Mensagens de validação em português brasileiro.

Utilize uma paleta profissional, com base neutra e uma cor de destaque consistente. Implemente tema claro e escuro se isso não comprometer o cronograma.

Telas necessárias:
1. Login.
2. Cadastro de organização.
3. Dashboard.
4. Contatos.
5. Detalhes do contato.
6. Empresas.
7. Leads.
8. Pipeline Kanban.
9. Detalhes do negócio.
10. Tarefas.
11. Agenda.
12. Produtos e serviços.
13. Propostas.
14. Relatórios.
15. Notificações.
16. Usuários e permissões.
17. Configurações da organização.

Não utilize gráficos decorativos sem significado, emojis como ícones principais, botões sem função ou dados fictícios apresentados como reais.

Utilize dados de demonstração somente em ambiente seed claramente identificado.

## 16. SEGURANÇA E QUALIDADE

Implemente obrigatoriamente:
- Hash de senhas.
- Autorização no backend.
- Isolamento entre organizações.
- Validação de entradas.
- Tratamento global de exceções.
- Respostas de erro padronizadas.
- Paginação nas listagens.
- Consultas eficientes.
- Transações nas operações críticas.
- Proteção contra atribuição indevida de campos sensíveis.
- Segredos exclusivamente em variáveis de ambiente.
- Logs sem senhas, tokens ou dados pessoais desnecessários.
- CORS restrito.
- Estratégia segura de sessão e tokens.
- Proteção contra abuso de endpoints sensíveis.
- Auditoria das operações importantes.

Não exponha entidades JPA diretamente nos controllers. Utilize DTOs de entrada e saída.

Não confie em IDs enviados pelo frontend para determinar a organização, o proprietário de um recurso ou o perfil de um usuário.

Crie testes para:
- Regras de negócio.
- Autenticação.
- Permissões.
- Isolamento multiempresa.
- Conversão de leads.
- Movimentação de negócios.
- Cálculo de valores.
- Paginação e filtros.
- Tratamento de erros.
- Persistência e relacionamentos.

## 17. TESTES E INTEGRAÇÃO CONTÍNUA

Configure:
- Testes unitários com JUnit 5 e Mockito.
- Testes de integração com Spring Boot Test e Testcontainers.
- Testes de controllers e endpoints críticos.
- Testes de componentes e fluxos essenciais do frontend.
- Lint e verificação de tipos no frontend.
- Compilação do backend.
- Pipeline GitHub Actions.

A cada pull request, executar:
1. Compilação do Java.
2. Testes do backend.
3. Verificação de tipos e lint do frontend.
4. Testes do frontend.
5. Build de produção.

Não ignore falhas para forçar uma execução verde.

## 18. DADOS DE DEMONSTRAÇÃO

Crie um perfil de seed exclusivo para desenvolvimento e demonstração.

Inclua dados fictícios claramente identificados:
- Uma organização de demonstração.
- Usuários com diferentes permissões.
- Contatos e empresas.
- Leads em diferentes estados.
- Negócios em várias etapas.
- Tarefas pendentes e concluídas.
- Atividades e propostas.

Nunca use dados reais de clientes.

Se criar credenciais de demonstração, documente como configurá-las e impeça que senhas padrão sejam habilitadas automaticamente em produção.

O dashboard deve exibir dados provenientes do banco, inclusive no ambiente de demonstração.

## 19. DOCUMENTAÇÃO E README

Crie um README.md profissional em português brasileiro, contendo:

1. Nome e descrição do projeto.
2. Problema que o CRM resolve.
3. Funcionalidades.
4. Tecnologias.
5. Arquitetura.
6. Diagrama de componentes.
7. Modelo de dados.
8. Pré-requisitos.
9. Instruções para executar com Docker Compose.
10. Configuração de variáveis de ambiente.
11. Como executar os testes.
12. Documentação da API.
13. Estrutura de diretórios.
14. Regras de negócio importantes.
15. Medidas de segurança.
16. Capturas de tela reais.
17. Limitações conhecidas.
18. Roadmap de melhorias.
19. Instruções para contribuir.
20. Licença.

Adicione um arquivo `.env.example` sem segredos reais.

Documente comandos executáveis, portas utilizadas e como inicializar o banco de dados. O README deve ser validado seguindo as instruções em uma instalação limpa.

## 20. CRONOGRAMA DE DESENVOLVIMENTO

Organize o trabalho em seis semanas, priorizando funcionalidades que demonstrem competência técnica.

### Semana 1 — Fundação
- Estrutura do repositório.
- Configuração Java, Spring Boot, React e PostgreSQL.
- Docker Compose.
- Migrations e entidades iniciais.
- Primeiros endpoints e testes.

### Semana 2 — Segurança e contatos
- Autenticação.
- Usuários e permissões.
- Isolamento multiempresa.
- Cadastro de contatos e empresas.
- Testes de autorização.

### Semana 3 — Leads e pipeline
- Gestão de leads.
- Conversão de leads.
- Negócios e etapas.
- Kanban.
- Histórico e regras de negócio.

### Semana 4 — Produtividade
- Tarefas.
- Atividades.
- Agenda.
- Dashboard e indicadores.
- Filtros e paginação.

### Semana 5 — Recursos comerciais
- Produtos e serviços.
- Propostas.
- Relatórios.
- Exportação CSV.
- Notificações internas.

### Semana 6 — Finalização
- Testes de integração.
- Correção de falhas.
- Revisão de segurança.
- Responsividade e acessibilidade.
- GitHub Actions.
- Documentação.
- Deploy e capturas de tela.

O cronograma é uma meta, não uma justificativa para entregar funcionalidades incompletas. Caso o prazo aperte, preserve autenticação, isolamento de dados, fluxo comercial principal, testes e documentação. Adie notificações avançadas, agenda sofisticada e PDF para uma segunda versão.

## 21. MÉTODO DE EXECUÇÃO

Não tente gerar todo o sistema em uma única operação sem validação.

Siga o processo:

1. Inspecione o repositório e identifique o que já existe.
2. Apresente a arquitetura e o plano de implementação.
3. Defina as entidades, os relacionamentos e as regras de negócio.
4. Configure a infraestrutura e o banco de dados.
5. Implemente um módulo por vez.
6. Escreva testes junto com cada funcionalidade.
7. Execute os testes e corrija as falhas antes de continuar.
8. Integre o frontend gradualmente com a API real.
9. Teste os fluxos completos como usuário.
10. Revise segurança, performance, acessibilidade e responsividade.
11. Documente tudo o que foi implementado.
12. Prepare a aplicação para execução local e demonstração pública.

Ao final de cada etapa, informe:
- O que foi implementado.
- Arquivos criados ou alterados.
- Testes executados e seus resultados reais.
- Problemas encontrados.
- Pendências.
- Próxima etapa recomendada.

Não declare que testes passaram, funcionalidades estão prontas ou o deploy foi concluído sem executar e verificar essas ações.

Não deixe botões sem funcionalidade, endpoints simulados ou dados estáticos no lugar de funcionalidades prometidas.

## 22. CRITÉRIOS DE ACEITAÇÃO

O projeto será considerado pronto para a primeira versão quando:

- Um usuário conseguir criar uma organização e autenticar-se.
- Usuários de organizações diferentes não conseguirem acessar dados uns dos outros.
- Contatos, empresas, leads e negócios forem persistidos no PostgreSQL.
- Um lead puder ser convertido em contato e negócio sem duplicações.
- O pipeline permitir movimentar negócios com validação no backend.
- Tarefas e atividades puderem ser criadas e concluídas.
- O dashboard apresentar indicadores calculados com dados reais.
- Permissões forem verificadas no backend.
- Testes automatizados cobrirem as regras críticas.
- O projeto executar localmente com instruções documentadas.
- O frontend for responsivo e apresentar estados de erro e carregamento.
- O GitHub Actions executar as verificações de qualidade.
- O README permitir que outra pessoa execute o projeto do zero.

## RESULTADO ESPERADO

Entregue um CRM funcional, com código limpo, arquitetura compreensível, interface profissional, backend Java real, banco PostgreSQL, segurança, testes e documentação.

Priorize qualidade, consistência e funcionalidades demonstráveis em entrevista técnica.

Comece inspecionando o projeto existente e apresentando o plano técnico. Em seguida, implemente a fundação e avance por etapas verificáveis, sem substituir o backend Java por outra tecnologia e sem esconder limitações ou falhas.
</USER_REQUEST>
<ADDITIONAL_METADATA>
The current local time is: 2026-10-09T17:05:11-03:00.
</ADDITIONAL_METADATA>