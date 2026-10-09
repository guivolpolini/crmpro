package com.crmpro.ai.controller;

import com.crmpro.ai.dto.AiPitchResponse;
import com.crmpro.ai.dto.GeneratePitchRequest;
import com.crmpro.ai.service.CommercialAiService;
import com.crmpro.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "Inteligência Artificial Comercial", description = "Endpoints para geração de pitches, e-mails de abordagem e auxílio cognitivo de vendas")
public class AiController {

    private final CommercialAiService aiService;

    @PostMapping("/pitch")
    @Operation(summary = "Gerar pitch comercial persuasivo personalizado via LLM")
    public ResponseEntity<ApiResponse<AiPitchResponse>> generatePitch(
            @Valid @RequestBody GeneratePitchRequest request
    ) {
        AiPitchResponse response = aiService.generatePitch(request);
        return ResponseEntity.ok(ApiResponse.ok("Pitch comercial gerado com sucesso", response));
    }
}
