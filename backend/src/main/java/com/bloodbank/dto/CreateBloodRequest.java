package com.bloodbank.dto;

import com.bloodbank.entity.enums.BloodGroup;
import com.bloodbank.entity.enums.ComponentType;
import com.bloodbank.entity.enums.RequestPriority;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBloodRequest {
    @NotBlank
    private String requestingHospital;
    
    private String patientName;
    
    @NotNull
    private BloodGroup patientBloodGroup;
    
    @NotNull
    private ComponentType componentType;
    
    @Min(1)
    private Integer unitsRequested;
    
    @NotNull
    private RequestPriority priority;
}
