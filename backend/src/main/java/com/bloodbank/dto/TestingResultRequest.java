package com.bloodbank.dto;

import lombok.Data;

@Data
public class TestingResultRequest {
    private boolean testedForHiv;
    private boolean testedForHepatitis;
    private boolean testedForMalaria;
    private boolean testedForSyphilis;
}
