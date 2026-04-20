package com.bloodbank.dto;

import com.bloodbank.entity.enums.ComponentType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CollectBloodRequest {
    @NotNull
    private Long donorId;
    
    @NotNull
    private ComponentType componentType;
    
    @Min(350)
    @Max(450)
    private Integer volumeML;

    @NotNull
    private Double donorWeight;

    @NotNull
    private Double donorHemoglobin;
}
