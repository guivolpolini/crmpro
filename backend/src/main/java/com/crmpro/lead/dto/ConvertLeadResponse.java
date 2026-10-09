package com.crmpro.lead.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ConvertLeadResponse {

    private UUID leadId;
    private UUID contactId;
    private UUID companyId;
    private Instant convertedAt;
    private String message;
}
