package com.crmpro.company.controller;

import com.crmpro.common.response.ApiResponse;
import com.crmpro.company.dto.CompanyRequest;
import com.crmpro.company.dto.CompanyResponse;
import com.crmpro.company.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Tag(name = "Empresas", description = "Endpoints para gestão de empresas clientes")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    @Operation(summary = "Listar empresas com paginação e filtro por nome")
    public ResponseEntity<ApiResponse<Page<CompanyResponse>>> findAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        Page<CompanyResponse> page = companyService.findAll(search, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar empresa por ID")
    public ResponseEntity<ApiResponse<CompanyResponse>> findById(@PathVariable UUID id) {
        CompanyResponse response = companyService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping
    @Operation(summary = "Criar nova empresa")
    public ResponseEntity<ApiResponse<CompanyResponse>> create(@Valid @RequestBody CompanyRequest request) {
        CompanyResponse response = companyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Empresa criada com sucesso", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar empresa existente")
    public ResponseEntity<ApiResponse<CompanyResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CompanyRequest request
    ) {
        CompanyResponse response = companyService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Empresa atualizada com sucesso", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover empresa")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        companyService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Empresa removida com sucesso", null));
    }
}
