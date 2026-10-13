package com.crmpro.lead.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LeadCsvImportResult {
    private int totalProcessed;
    private int successCount;
    private int errorCount;
    private List<String> errorMessages;
}
