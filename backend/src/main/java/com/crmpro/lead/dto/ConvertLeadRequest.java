package com.crmpro.lead.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConvertLeadRequest {

    private boolean createCompany = true;
    private String companyName;
    private boolean createDeal = false;
    private String dealTitle;
    private BigDecimal dealAmount;
}
