package com.crmpro.activity.controller;

import com.crmpro.activity.dto.ActivityRequest;
import com.crmpro.activity.dto.ActivityResponse;
import com.crmpro.activity.service.ActivityService;
import com.crmpro.common.response.ApiResponse;
import com.crmpro.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
@Tag(name = "Atividades & Timeline", description = "Endpoints para registro de histórico de interações (ligações, reuniões, notas)")
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    @Operation(summary = "Listar atividades recentes da organização")
    public ResponseEntity<ApiResponse<List<ActivityResponse>>> findAll() {
        List<ActivityResponse> activities = activityService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(activities));
    }

    @GetMapping("/by-deal/{dealId}")
    @Operation(summary = "Listar histórico de atividades vinculadas a uma oportunidade específica")
    public ResponseEntity<ApiResponse<List<ActivityResponse>>> findByDeal(@PathVariable UUID dealId) {
        List<ActivityResponse> activities = activityService.findByDeal(dealId);
        return ResponseEntity.ok(ApiResponse.ok(activities));
    }

    @PostMapping
    @Operation(summary = "Registrar nova interação ou nota")
    public ResponseEntity<ApiResponse<ActivityResponse>> create(
            @Valid @RequestBody ActivityRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ActivityResponse response = activityService.create(request, principal != null ? principal.getId() : null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Atividade registrada com sucesso", response));
    }
}
