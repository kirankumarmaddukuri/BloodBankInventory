package com.bloodbank.dto;

import com.bloodbank.entity.enums.BloodGroup;
import com.bloodbank.entity.enums.ComponentType;
import com.bloodbank.entity.enums.UrgencyLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TriggerEmergencyRequest {
    @NotNull
    private BloodGroup requiredBloodGroup;
    
    @NotNull
    private ComponentType requiredComponentType;
    
    @Min(1)
    private Integer unitsNeeded;
    
    @NotBlank
    private String location;
    
    @NotNull
    private UrgencyLevel urgency;
}
