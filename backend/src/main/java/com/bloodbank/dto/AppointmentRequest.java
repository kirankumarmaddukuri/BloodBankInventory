package com.bloodbank.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {
    @NotNull(message = "Appointment date cannot be null")
    @Future(message = "Appointment must be in the future")
    private LocalDateTime appointmentDate;
    
    private String notes;
}
