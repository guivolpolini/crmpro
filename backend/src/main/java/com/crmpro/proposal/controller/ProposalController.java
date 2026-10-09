package com.crmpro.proposal.controller;

import com.crmpro.common.response.ApiResponse;
import com.crmpro.proposal.dto.ProductRequest;
import com.crmpro.proposal.dto.ProductResponse;
import com.crmpro.proposal.dto.ProposalRequest;
import com.crmpro.proposal.dto.ProposalResponse;
import com.crmpro.proposal.service.ProposalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/proposals")
@RequiredArgsConstructor
@Tag(name = "Propostas & Produtos", description = "Endpoints para gerenciamento de catálogo de produtos e emissão de propostas comerciais")
public class ProposalController {

    private final ProposalService proposalService;

    // --- Products ---

    @GetMapping("/products")
    @Operation(summary = "Listar produtos/serviços ativos da organização")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findAllProducts() {
        List<ProductResponse> products = proposalService.findAllProducts();
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @PostMapping("/products")
    @Operation(summary = "Cadastrar novo produto ou serviço no catálogo")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = proposalService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Produto cadastrado com sucesso", response));
    }

    // --- Proposals ---

    @GetMapping
    @Operation(summary = "Listar todas as propostas comerciais emitidas")
    public ResponseEntity<ApiResponse<List<ProposalResponse>>> findAllProposals() {
        List<ProposalResponse> proposals = proposalService.findAllProposals();
        return ResponseEntity.ok(ApiResponse.ok(proposals));
    }

    @GetMapping("/by-deal/{dealId}")
    @Operation(summary = "Listar propostas vinculadas a uma oportunidade específica")
    public ResponseEntity<ApiResponse<List<ProposalResponse>>> findProposalsByDeal(@PathVariable UUID dealId) {
        List<ProposalResponse> proposals = proposalService.findProposalsByDeal(dealId);
        return ResponseEntity.ok(ApiResponse.ok(proposals));
    }

    @PostMapping
    @Operation(summary = "Emitir nova proposta comercial")
    public ResponseEntity<ApiResponse<ProposalResponse>> createProposal(@Valid @RequestBody ProposalRequest request) {
        ProposalResponse response = proposalService.createProposal(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Proposta comercial emitida com sucesso", response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar situação da proposta (DRAFT, SENT, ACCEPTED, REJECTED)")
    public ResponseEntity<ApiResponse<ProposalResponse>> updateStatus(
            @PathVariable UUID id,
            @RequestParam String status
    ) {
        ProposalResponse response = proposalService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Status da proposta atualizado", response));
    }
}
