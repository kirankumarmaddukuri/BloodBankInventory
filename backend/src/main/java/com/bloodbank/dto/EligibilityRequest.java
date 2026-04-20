package com.bloodbank.dto;

import com.bloodbank.entity.enums.EligibilityStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EligibilityRequest {
    @NotNull(message = "Eligibility status is required")
    private EligibilityStatus status;
}
