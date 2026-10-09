package com.crmpro.report.controller;

import com.crmpro.common.response.ApiResponse;
import com.crmpro.report.dto.PerformanceReportResponse;
import com.crmpro.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Relatórios & Performance", description = "Relatórios gerenciais de vendas, métricas de equipe e exportação CSV")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/performance")
    @Operation(summary = "Obter relatório consolidado de performance comercial")
    public ResponseEntity<ApiResponse<PerformanceReportResponse>> getPerformanceReport() {
        PerformanceReportResponse report = reportService.getPerformanceReport();
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @GetMapping(value = "/export/deals", produces = "text/csv")
    @Operation(summary = "Exportar lista de oportunidades comerciais em CSV")
    public ResponseEntity<String> exportDealsCsv() {
        String csv = reportService.exportDealsToCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"deals_export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }
}
