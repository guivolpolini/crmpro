package com.crmpro.ai.service;

import com.crmpro.ai.dto.AiPitchResponse;
import com.crmpro.ai.dto.GeneratePitchRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CommercialAiService {

    @Value("${ai.openai.base-url:http://localhost:3001/v1}")
    private String baseUrl;

    @Value("${ai.openai.api-key:default_key}")
    private String apiKey;

    @Value("${ai.openai.model:auto}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiPitchResponse generatePitch(GeneratePitchRequest request) {
        String prompt = String.format(
                "Você é um especialista em vendas consultivas B2B de alta conversão. " +
                "Crie uma abordagem comercial direta para o lead '%s', setor '%s'. " +
                "Dor do cliente: '%s'. Produto ofertado: '%s'. " +
                "Responda apenas com o texto do e-mail/pitch pronto para envio com Assunto, Corpo e Chamada para Ação.",
                request.getRecipientName(),
                request.getSegment() != null ? request.getSegment() : "Geral",
                request.getPainPoints() != null ? request.getPainPoints() : "Aumento de eficiência operacional",
                request.getTargetProductName() != null ? request.getTargetProductName() : "CRM PRO Enterprise"
        );

        try {
            RestClient client = RestClient.builder()
                    .baseUrl(baseUrl)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", "Você é um assistente comercial B2B persuasivo e objetivo."),
                            Map.of("role", "user", "content", prompt)
                    ),
                    "temperature", 0.7
            );

            String response = client.post()
                    .uri("/chat/completions")
                    .body(body)
                    .retrieve()
                    .body(String.class);

            if (response != null) {
                JsonNode root = objectMapper.readTree(response);
                String content = root.path("choices").get(0).path("message").path("content").asText();
                return buildResponseFromContent(request, content);
            }
        } catch (Exception ex) {
            log.warn("FreeLLMAPI indisponível ou em fallback, gerando pitch pelo template inteligente embutido: {}", ex.getMessage());
        }

        // Fallback robusto caso servidor local de LLM esteja offline
        return generateDeterministicPitch(request);
    }

    private AiPitchResponse buildResponseFromContent(GeneratePitchRequest request, String content) {
        String subject = "Oportunidade de otimização operacional para " + request.getRecipientName();
        String cta = "Podemos agendar uma demonstração rápida de 15 minutos nesta semana?";

        return AiPitchResponse.builder()
                .subject(subject)
                .pitchText(content)
                .callToAction(cta)
                .build();
    }

    private AiPitchResponse generateDeterministicPitch(GeneratePitchRequest request) {
        String recipient = request.getRecipientName();
        String product = request.getTargetProductName() != null ? request.getTargetProductName() : "CRM PRO";
        String pain = request.getPainPoints() != null ? request.getPainPoints() : "perda de visibilidade comercial e gargalos no funil de vendas";

        String text = String.format(
                "Olá, %s,\n\n" +
                "Notei os desafios atuais da sua operação relacionados a %s.\n\n" +
                "Empresas do seu setor estão utilizando o %s para centralizar o pipeline de vendas, " +
                "eliminar retrabalho e aumentar a conversão de oportunidades em até 30%%.\n\n" +
                "Gostaria de compartilhar como nossos clientes reduziram o ciclo de fechamento sem complicação.",
                recipient, pain, product
        );

        return AiPitchResponse.builder()
                .subject("Otimização de processos comerciais | " + recipient)
                .pitchText(text)
                .callToAction("Teria 15 minutos nesta quinta-feira para avaliarmos a aplicabilidade no seu cenário?")
                .build();
    }
}
