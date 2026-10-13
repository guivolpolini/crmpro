package com.crmpro.proposal.service;

import com.crmpro.common.context.TenantContext;
import com.crmpro.common.exception.ResourceNotFoundException;
import com.crmpro.deal.entity.Deal;
import com.crmpro.deal.repository.DealRepository;
import com.crmpro.organization.entity.Organization;
import com.crmpro.organization.repository.OrganizationRepository;
import com.crmpro.proposal.entity.Proposal;
import com.crmpro.proposal.repository.ProposalRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProposalPdfService {

    private final ProposalRepository proposalRepository;
    private final DealRepository dealRepository;
    private final OrganizationRepository organizationRepository;

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional(readOnly = true)
    public byte[] generateProposalPdf(UUID proposalId) {
        UUID tenantId = TenantContext.getTenantId();

        Proposal proposal = proposalRepository.findById(proposalId)
                .filter(p -> p.getOrganizationId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Proposta comercial não encontrada"));

        Deal deal = dealRepository.findByIdAndOrganizationId(proposal.getDealId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidade comercial vinculada não encontrada"));

        Organization org = organizationRepository.findById(tenantId).orElse(null);
        String orgName = org != null ? org.getName() : "CRM PRO";

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 40, 40);
            PdfWriter.getInstance(document, out);
            document.open();

            // Fontes
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(30, 41, 59));
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(100, 116, 139));
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(15, 23, 42));
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(51, 65, 85));
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(30, 41, 59));

            // Cabeçalho da Empresa
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{60, 40});

            PdfPCell cellLeft = new PdfPCell();
            cellLeft.setBorder(Rectangle.NO_BORDER);
            cellLeft.addElement(new Paragraph(orgName.toUpperCase(), titleFont));
            cellLeft.addElement(new Paragraph("Proposta Comercial Formal", subtitleFont));
            headerTable.addCell(cellLeft);

            PdfPCell cellRight = new PdfPCell();
            cellRight.setBorder(Rectangle.NO_BORDER);
            cellRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph codeP = new Paragraph("CÓDIGO: " + proposal.getCode(), boldFont);
            codeP.setAlignment(Element.ALIGN_RIGHT);
            Paragraph statusP = new Paragraph("STATUS: " + proposal.getStatus(), subtitleFont);
            statusP.setAlignment(Element.ALIGN_RIGHT);
            String emissionDate = proposal.getCreatedAt() != null
                    ? DATE_FORMAT.format(proposal.getCreatedAt().atZone(java.time.ZoneId.systemDefault()))
                    : "Data não informada";
            Paragraph dateP = new Paragraph("EMISSÃO: " + emissionDate, subtitleFont);
            dateP.setAlignment(Element.ALIGN_RIGHT);
            cellRight.addElement(codeP);
            cellRight.addElement(statusP);
            cellRight.addElement(dateP);
            headerTable.addCell(cellRight);

            document.add(headerTable);
            document.add(new Paragraph(" "));

            // Linha divisória
            PdfPTable divider = new PdfPTable(1);
            divider.setWidthPercentage(100);
            PdfPCell divCell = new PdfPCell();
            divCell.setBackgroundColor(new Color(37, 99, 235));
            divCell.setFixedHeight(2f);
            divCell.setBorder(Rectangle.NO_BORDER);
            divider.addCell(divCell);
            document.add(divider);
            document.add(new Paragraph(" "));

            // Bloco de Dados do Cliente / Negócio
            document.add(new Paragraph("1. DADOS DA OPORTUNIDADE & CLIENTE", sectionFont));
            document.add(new Paragraph(" "));

            PdfPTable clientTable = new PdfPTable(2);
            clientTable.setWidthPercentage(100);
            clientTable.setWidths(new float[]{50, 50});

            clientTable.addCell(createKeyValCell("Negócio / Projeto:", deal.getTitle(), normalFont, boldFont));
            clientTable.addCell(createKeyValCell("Validade da Proposta:",
                    proposal.getValidUntil() != null ? proposal.getValidUntil().format(DATE_FORMAT) : "30 dias",
                    normalFont, boldFont));
            document.add(clientTable);
            document.add(new Paragraph(" "));

            // Bloco de Valores e Itens
            document.add(new Paragraph("2. ESCOPO & CONDIÇÕES COMERCIAIS", sectionFont));
            document.add(new Paragraph(" "));

            PdfPTable itemsTable = new PdfPTable(3);
            itemsTable.setWidthPercentage(100);
            itemsTable.setWidths(new float[]{60, 20, 20});

            // Cabeçalho da tabela
            itemsTable.addCell(createHeaderCell("Descrição dos Serviços / Fornecimento"));
            itemsTable.addCell(createHeaderCell("Desconto"));
            itemsTable.addCell(createHeaderCell("Valor Final"));

            // Linha única com o escopo global
            PdfPCell itemDesc = new PdfPCell(new Phrase(proposal.getNotes() != null && !proposal.getNotes().isBlank()
                    ? proposal.getNotes()
                    : "Fornecimento de serviços e soluções conforme alinhamento comercial.", normalFont));
            itemDesc.setPadding(8);
            itemDesc.setBorderColor(new Color(226, 232, 240));
            itemsTable.addCell(itemDesc);

            BigDecimal discount = proposal.getDiscount() != null ? proposal.getDiscount() : BigDecimal.ZERO;
            PdfPCell itemDisc = new PdfPCell(new Phrase(CURRENCY_FORMAT.format(discount), normalFont));
            itemDisc.setPadding(8);
            itemDisc.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemDisc.setBorderColor(new Color(226, 232, 240));
            itemsTable.addCell(itemDisc);

            PdfPCell itemVal = new PdfPCell(new Phrase(CURRENCY_FORMAT.format(proposal.getTotalAmount()), boldFont));
            itemVal.setPadding(8);
            itemVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemVal.setBorderColor(new Color(226, 232, 240));
            itemsTable.addCell(itemVal);

            document.add(itemsTable);
            document.add(new Paragraph(" "));

            // Bloco de Totalizadores
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(40);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.setWidths(new float[]{50, 50});

            PdfPCell totalLabel = new PdfPCell(new Phrase("INVESTIMENTO TOTAL:", boldFont));
            totalLabel.setBackgroundColor(new Color(241, 245, 249));
            totalLabel.setPadding(6);
            totalLabel.setBorder(Rectangle.NO_BORDER);

            PdfPCell totalVal = new PdfPCell(new Phrase(CURRENCY_FORMAT.format(proposal.getTotalAmount()),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(37, 99, 235))));
            totalVal.setBackgroundColor(new Color(241, 245, 249));
            totalVal.setPadding(6);
            totalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalVal.setBorder(Rectangle.NO_BORDER);

            totalTable.addCell(totalLabel);
            totalTable.addCell(totalVal);
            document.add(totalTable);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Assinaturas e Aceite
            document.add(new Paragraph("3. ACEITE E FORMALIZAÇÃO", sectionFont));
            document.add(new Paragraph("Ao assinar abaixo, o contratante declara ciência e aceite dos termos aqui estipulados.", subtitleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            PdfPTable signTable = new PdfPTable(2);
            signTable.setWidthPercentage(100);
            signTable.setWidths(new float[]{45, 45});

            PdfPCell sign1 = new PdfPCell();
            sign1.setBorder(Rectangle.NO_BORDER);
            Paragraph line1 = new Paragraph("________________________________________", normalFont);
            Paragraph name1 = new Paragraph(orgName + "\nRepresentante Comercial", subtitleFont);
            line1.setAlignment(Element.ALIGN_CENTER);
            name1.setAlignment(Element.ALIGN_CENTER);
            sign1.addElement(line1);
            sign1.addElement(name1);

            PdfPCell sign2 = new PdfPCell();
            sign2.setBorder(Rectangle.NO_BORDER);
            Paragraph line2 = new Paragraph("________________________________________", normalFont);
            Paragraph name2 = new Paragraph("Aceite do Cliente / Assinatura Autorizada\nData: ____/____/________", subtitleFont);
            line2.setAlignment(Element.ALIGN_CENTER);
            name2.setAlignment(Element.ALIGN_CENTER);
            sign2.addElement(line2);
            sign2.addElement(name2);

            signTable.addCell(sign1);
            signTable.addCell(sign2);
            document.add(signTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Erro ao gerar PDF da proposta comercial: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na geração do documento PDF da proposta", e);
        }
    }

    private PdfPCell createHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
        cell.setBackgroundColor(new Color(30, 41, 59));
        cell.setPadding(6);
        cell.setBorderColor(new Color(30, 41, 59));
        return cell;
    }

    private PdfPCell createKeyValCell(String label, String value, Font labelFont, Font valFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " ", labelFont));
        p.add(new Chunk(value != null ? value : "-", valFont));
        cell.addElement(p);
        return cell;
    }
}
