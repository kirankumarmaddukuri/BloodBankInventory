package com.bloodbank.controller;

import com.bloodbank.dto.CreateBloodRequest;
import com.bloodbank.entity.BloodRequest;
import com.bloodbank.entity.enums.RequestStatus;
import com.bloodbank.repository.BloodRequestRepository;
import com.bloodbank.security.CustomUserDetails;
import com.bloodbank.service.BloodRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-requests")
@RequiredArgsConstructor
public class BloodRequestController {

    private final BloodRequestService bloodRequestService;
    private final BloodRequestRepository bloodRequestRepository;

    @PostMapping
    @PreAuthorize("hasRole('HOSPITAL_STAFF')")
    public ResponseEntity<BloodRequest> createRequest(@Valid @RequestBody CreateBloodRequest request) {
        return ResponseEntity.ok(bloodRequestService.createRequest(
                request.getRequestingHospital(),
                request.getPatientName(),
                request.getPatientBloodGroup(),
                request.getComponentType(),
                request.getUnitsRequested(),
                request.getPriority()
        ));
    }

    @PutMapping("/{id}/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'BLOOD_BANK_ADMIN')")
    public ResponseEntity<BloodRequest> processRequest(@PathVariable Long id) {
        return ResponseEntity.ok(bloodRequestService.processRequest(id));
    }

    @PutMapping("/{id}/fulfill")
    @PreAuthorize("hasAnyRole('ADMIN', 'BLOOD_BANK_ADMIN')")
    public ResponseEntity<BloodRequest> fulfillRequest(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bloodRequestService.fulfillRequest(id, userDetails.getUser()));
    }

    @GetMapping
    public ResponseEntity<List<BloodRequest>> getAllRequests() {
        return ResponseEntity.ok(bloodRequestRepository.findAll());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'BLOOD_BANK_ADMIN')")
    public ResponseEntity<List<BloodRequest>> getPendingRequests() {
        return ResponseEntity.ok(bloodRequestRepository.findByStatus(RequestStatus.PENDING));
    }
}
