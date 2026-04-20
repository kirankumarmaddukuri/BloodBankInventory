package com.bloodbank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransfusionLogRequest {
    @NotNull
    private Long bloodUnitId;
    
    @NotNull
    private Long requestId;
    
    private String patientName;
    private String hospital;
    
    @NotBlank
    private String transfusedBy;
    
    private boolean adverseReaction;
    private String reactionRemarks;
}
