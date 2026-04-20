package com.bloodbank.controller;

import com.bloodbank.dto.TriggerEmergencyRequest;
import com.bloodbank.entity.DonorNotification;
import com.bloodbank.entity.EmergencyAlert;
import com.bloodbank.entity.enums.AlertStatus;
import com.bloodbank.repository.DonorNotificationRepository;
import com.bloodbank.repository.EmergencyAlertRepository;
import com.bloodbank.security.CustomUserDetails;
import com.bloodbank.service.EmergencyMatchingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergency-alerts")
@RequiredArgsConstructor
public class EmergencyAlertController {

    private final EmergencyMatchingService emergencyMatchingService;
    private final EmergencyAlertRepository emergencyAlertRepository;
    private final DonorNotificationRepository donorNotificationRepository;

    @PostMapping
    @PreAuthorize("hasRole('EMERGENCY_COORDINATOR')")
    public ResponseEntity<EmergencyAlert> triggerAlert(
            @Valid @RequestBody TriggerEmergencyRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(emergencyMatchingService.triggerEmergency(
                userDetails.getUser(),
                request.getRequiredBloodGroup(),
                request.getRequiredComponentType(),
                request.getUnitsNeeded(),
                request.getLocation(),
                request.getUrgency()
        ));
    }

    @GetMapping("/active")
    public ResponseEntity<List<EmergencyAlert>> getActiveAlerts() {
        return ResponseEntity.ok(emergencyAlertRepository.findByStatus(AlertStatus.ACTIVE));
    }

    @PutMapping("/{id}/fulfill")
    @PreAuthorize("hasRole('EMERGENCY_COORDINATOR')")
    public ResponseEntity<EmergencyAlert> fulfillAlert(@PathVariable Long id) {
        return ResponseEntity.ok(emergencyMatchingService.fulfillAlert(id));
    }

    @GetMapping("/{id}/donor-match")
    @PreAuthorize("hasRole('EMERGENCY_COORDINATOR')")
    public ResponseEntity<List<DonorNotification>> getDonorMatches(@PathVariable Long id) {
        return ResponseEntity.ok(donorNotificationRepository.findByEmergencyAlertId(id));
    }
}
