package com.crmpro.ai.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiPitchResponse {
    private String subject;
    private String pitchText;
    private String callToAction;
}
