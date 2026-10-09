package com.crmpro.contact.controller;

import com.crmpro.common.response.ApiResponse;
import com.crmpro.contact.dto.ContactRequest;
import com.crmpro.contact.dto.ContactResponse;
import com.crmpro.contact.service.ContactService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
@Tag(name = "Contatos", description = "Endpoints para gestão de contatos de clientes")
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    @Operation(summary = "Listar contatos com paginação e filtro por nome")
    public ResponseEntity<ApiResponse<Page<ContactResponse>>> findAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        Page<ContactResponse> page = contactService.findAll(search, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar contato por ID")
    public ResponseEntity<ApiResponse<ContactResponse>> findById(@PathVariable UUID id) {
        ContactResponse response = contactService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/by-company/{companyId}")
    @Operation(summary = "Listar todos os contatos de uma determinada empresa")
    public ResponseEntity<ApiResponse<List<ContactResponse>>> findByCompany(@PathVariable UUID companyId) {
        List<ContactResponse> contacts = contactService.findByCompany(companyId);
        return ResponseEntity.ok(ApiResponse.ok(contacts));
    }

    @PostMapping
    @Operation(summary = "Criar novo contato")
    public ResponseEntity<ApiResponse<ContactResponse>> create(@Valid @RequestBody ContactRequest request) {
        ContactResponse response = contactService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Contato criado com sucesso", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar contato existente")
    public ResponseEntity<ApiResponse<ContactResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody ContactRequest request
    ) {
        ContactResponse response = contactService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Contato atualizado com sucesso", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover contato")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        contactService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Contato removido com sucesso", null));
    }
}
