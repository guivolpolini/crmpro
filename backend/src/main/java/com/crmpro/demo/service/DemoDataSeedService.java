package com.crmpro.demo.service;

import com.crmpro.common.context.TenantContext;
import com.crmpro.company.entity.Company;
import com.crmpro.company.repository.CompanyRepository;
import com.crmpro.contact.entity.Contact;
import com.crmpro.contact.repository.ContactRepository;
import com.crmpro.deal.entity.Deal;
import com.crmpro.deal.entity.DealStatus;
import com.crmpro.deal.repository.DealRepository;
import com.crmpro.lead.entity.Lead;
import com.crmpro.lead.entity.LeadStatus;
import com.crmpro.lead.repository.LeadRepository;
import com.crmpro.pipeline.entity.PipelineStage;
import com.crmpro.pipeline.service.PipelineStageService;
import com.crmpro.proposal.entity.Product;
import com.crmpro.proposal.entity.Proposal;
import com.crmpro.proposal.repository.ProductRepository;
import com.crmpro.proposal.repository.ProposalRepository;
import com.crmpro.task.entity.Task;
import com.crmpro.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeedService {

    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;
    private final PipelineStageService stageService;
    private final TaskRepository taskRepository;
    private final ProductRepository productRepository;
    private final ProposalRepository proposalRepository;

    @Transactional
    public void seedDemoDataForCurrentTenant() {
        UUID tenantId = TenantContext.getTenantId();
        log.info("Iniciando geração de dados de demonstração para a organização: {}", tenantId);

        // 1. Garantir estágios do Kanban
        List<PipelineStage> stages = stageService.initializeDefaultStages(tenantId);
        PipelineStage pProspeccao = stages.get(0);
        PipelineStage pQualificacao = stages.get(1);
        PipelineStage pApresentacao = stages.get(2);
        PipelineStage pNegociacao = stages.get(3);
        PipelineStage pGanho = stages.get(4);
        PipelineStage pPerdido = stages.get(5);

        // 2. Criar Empresas B2B
        Company comp1 = companyRepository.save(Company.builder()
                .name("TechLog Soluções em Logística S/A")
                .tradeName("TechLog Transportes Inteligentes")
                .document("45.123.890/0001-44")
                .segment("Logística & Supply Chain")
                .website("https://techlog.com.br")
                .build());

        Company comp2 = companyRepository.save(Company.builder()
                .name("Instituto Médico Hospitalar São Lucas Ltda")
                .tradeName("Rede Hospitalar São Lucas")
                .document("18.442.901/0001-72")
                .segment("Saúde & Medicina")
                .website("https://saolucas.med.br")
                .build());

        Company comp3 = companyRepository.save(Company.builder()
                .name("VarejoMais Distribuição Digital S/A")
                .tradeName("VarejoMais E-commerce Group")
                .document("33.891.205/0001-19")
                .segment("Varejo Digital")
                .website("https://varejomais.com.br")
                .build());

        // 3. Criar Contatos Decisores
        Contact cont1 = contactRepository.save(Contact.builder()
                .companyId(comp1.getId())
                .name("Rodrigo Mendes")
                .email("rodrigo.mendes@techlog.com.br")
                .phone("(11) 98765-4321")
                .jobTitle("Diretor de Operações")
                .build());

        Contact cont2 = contactRepository.save(Contact.builder()
                .companyId(comp2.getId())
                .name("Dra. Fernanda Albuquerque")
                .email("fernanda.albuquerque@saolucas.med.br")
                .phone("(21) 99123-8877")
                .jobTitle("Superintendente Geral")
                .build());

        Contact cont3 = contactRepository.save(Contact.builder()
                .companyId(comp3.getId())
                .name("Carlos Eduardo Rossi")
                .email("carlos.rossi@varejomais.com.br")
                .phone("(41) 98877-1122")
                .jobTitle("Head de Tecnologia")
                .build());

        // 4. Criar Leads em prospecção e qualificação
        leadRepository.save(Lead.builder()
                .name("Mariana Souza")
                .email("mariana.souza@agronegocios.com.br")
                .phone("(67) 99988-3344")
                .companyName("AgroForte Sementes")
                .status(LeadStatus.QUALIFIED)
                .source("Google Ads")
                .notes("Interessada na automação do pipeline comercial para representantes de campo.")
                .score(85)
                .build());

        leadRepository.save(Lead.builder()
                .name("Felipe Antunes")
                .email("felipe.antunes@fintechbrasil.com")
                .phone("(11) 97654-3210")
                .companyName("PayFast FinTech")
                .status(LeadStatus.CONTACTED)
                .source("LinkedIn")
                .notes("Reunião inicial de diagnóstico agendada.")
                .score(60)
                .build());

        leadRepository.save(Lead.builder()
                .name("Juliana Ribeiro")
                .email("juliana.ribeiro@construtorasp.com.br")
                .phone("(19) 98122-4455")
                .companyName("Aliança Empreendimentos")
                .status(LeadStatus.NEW)
                .source("Indicação Comercial")
                .notes("Lead recém recebido por recomendação de parceiro.")
                .score(50)
                .build());

        // 5. Criar Oportunidades (Deals) no Funil Kanban
        Deal d1 = dealRepository.save(Deal.builder()
                .stageId(pNegociacao.getId())
                .companyId(comp1.getId())
                .contactId(cont1.getId())
                .title("Implantação CRM PRO Enterprise + Roteirização")
                .amount(new BigDecimal("75000.00"))
                .probability(80)
                .expectedCloseDate(LocalDate.now().plusDays(15))
                .status(DealStatus.OPEN)
                .build());

        Deal d2 = dealRepository.save(Deal.builder()
                .stageId(pGanho.getId())
                .companyId(comp2.getId())
                .contactId(cont2.getId())
                .title("Licenciamento Anual Medical Suite São Lucas")
                .amount(new BigDecimal("120000.00"))
                .probability(100)
                .expectedCloseDate(LocalDate.now().minusDays(5))
                .closedAt(Instant.now().minusSeconds(86400 * 5))
                .status(DealStatus.WON)
                .build());

        Deal d3 = dealRepository.save(Deal.builder()
                .stageId(pApresentacao.getId())
                .companyId(comp3.getId())
                .contactId(cont3.getId())
                .title("Módulo de Integração E-commerce OmniChannel")
                .amount(new BigDecimal("42000.00"))
                .probability(60)
                .expectedCloseDate(LocalDate.now().plusDays(30))
                .status(DealStatus.OPEN)
                .build());

        Deal d4 = dealRepository.save(Deal.builder()
                .stageId(pProspeccao.getId())
                .title("Expansão Filiais Sudeste - 50 Contas")
                .amount(new BigDecimal("35000.00"))
                .probability(30)
                .expectedCloseDate(LocalDate.now().plusDays(45))
                .status(DealStatus.OPEN)
                .build());

        // 6. Criar Tarefas Operacionais
        taskRepository.save(Task.builder()
                .dealId(d1.getId())
                .contactId(cont1.getId())
                .title("Apresentar minuta do contrato e SLA para o Diretor Rodrigo")
                .description("Alinhar cláusulas de suporte 24/7 com o jurídico do cliente.")
                .dueDate(Instant.now().plusSeconds(86400 * 2))
                .priority("HIGH")
                .status("PENDING")
                .build());

        taskRepository.save(Task.builder()
                .dealId(d3.getId())
                .contactId(cont3.getId())
                .title("Enviar cronograma técnico de integração da API")
                .description("Documentação OpenAPI swagger enviada para validação.")
                .dueDate(Instant.now().plusSeconds(86400 * 4))
                .priority("MEDIUM")
                .status("PENDING")
                .build());

        // 7. Criar Produtos & Proposta Comercial
        Product prod1 = productRepository.save(Product.builder()
                .name("CRM PRO Enterprise License (Anual)")
                .code("PRD-ENT-001")
                .description("Plataforma completa com multi-tenancy e pipeline ilimitado")
                .unitPrice(new BigDecimal("60000.00"))
                .unit("ANO")
                .active(true)
                .build());

        Product prod2 = productRepository.save(Product.builder()
                .name("Serviço de Onboarding & Treinamento Executivo")
                .code("SRV-ONB-002")
                .description("Capacitação de 40 horas para gestores e equipe comercial")
                .unitPrice(new BigDecimal("15000.00"))
                .unit("PROJETO")
                .active(true)
                .build());

        proposalRepository.save(Proposal.builder()
                .dealId(d1.getId())
                .code("PROP-702914")
                .totalAmount(new BigDecimal("75000.00"))
                .discount(new BigDecimal("0.00"))
                .status("SENT")
                .validUntil(LocalDate.now().plusDays(20))
                .notes("Proposta contempla implantação acelerada em 15 dias úteis com garantia estendida.")
                .build());

        log.info("Carga de dados de demonstração concluída com sucesso para a organização: {}", tenantId);
    }
}
