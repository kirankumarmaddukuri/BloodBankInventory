package com.bloodbank.controller;

import com.bloodbank.entity.Donor;
import com.bloodbank.entity.enums.BloodGroup;
import com.bloodbank.entity.enums.EligibilityStatus;
import com.bloodbank.repository.DonorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bloodbank.dto.EligibilityRequest;
import com.bloodbank.entity.BloodUnit;
import com.bloodbank.entity.DonorNotification;
import com.bloodbank.entity.User;
import com.bloodbank.entity.enums.ResponseStatus;
import com.bloodbank.repository.BloodUnitRepository;
import com.bloodbank.repository.DonorNotificationRepository;
import com.bloodbank.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/donors")
@RequiredArgsConstructor
public class DonorController {

    private final DonorRepository donorRepository;
    private final BloodUnitRepository bloodUnitRepository;
    private final DonorNotificationRepository notificationRepository;

    // POST /api/donors/register is handled by AuthController

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BLOOD_BANK_ADMIN', 'EMERGENCY_COORDINATOR')")
    public ResponseEntity<List<Donor>> getAllDonors() {
        return ResponseEntity.ok(donorRepository.findAll());
    }

    @GetMapping("/eligible")
    @PreAuthorize("hasAnyRole('ADMIN', 'BLOOD_BANK_ADMIN', 'EMERGENCY_COORDINATOR')")
    public ResponseEntity<List<Donor>> getEligibleDonors(@RequestParam BloodGroup bloodGroup) {
        return ResponseEntity.ok(donorRepository.findByBloodGroupAndEligibilityStatus(bloodGroup, EligibilityStatus.ELIGIBLE));
    }

    @GetMapping("/my-history")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<List<BloodUnit>> getMyHistory(@AuthenticationPrincipal CustomUserDetails currUser) {
        Donor donor = donorRepository.findByUserId(currUser.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Donor profile not found"));
        return ResponseEntity.ok(bloodUnitRepository.findByDonorId(donor.getId()));
    }

    @PutMapping("/{id}/eligibility")
    @PreAuthorize("hasRole('BLOOD_BANK_ADMIN')")
    public ResponseEntity<Donor> updateEligibility(@PathVariable Long id, @Valid @RequestBody EligibilityRequest request) {
        Donor donor = donorRepository.findById(id).orElseThrow(() -> new RuntimeException("Donor not found"));
        donor.setEligibilityStatus(request.getStatus());
        return ResponseEntity.ok(donorRepository.save(donor));
    }

    @GetMapping("/my-profile")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<Donor> getMyProfile(@AuthenticationPrincipal CustomUserDetails currUser) {
        Donor donor = donorRepository.findByUserId(currUser.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Donor profile not found"));
        return ResponseEntity.ok(donor);
    }

    @GetMapping("/notifications")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<List<DonorNotification>> getMyNotifications(@AuthenticationPrincipal CustomUserDetails currUser) {
        Donor donor = donorRepository.findByUserId(currUser.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Donor profile not found"));
        return ResponseEntity.ok(notificationRepository.findByDonorId(donor.getId()));
    }

    @PutMapping("/notifications/{id}/respond")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<DonorNotification> respondToNotification(
            @PathVariable Long id, 
            @RequestParam ResponseStatus response,
            @AuthenticationPrincipal CustomUserDetails currUser) {
        DonorNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        // Ensure donor only responds to their own notification
        if (!notification.getDonor().getUser().getId().equals(currUser.getUser().getId())) {
             throw new RuntimeException("Unauthorized to modify this notification");
        }
        
        notification.setResponseStatus(response);
        return ResponseEntity.ok(notificationRepository.save(notification));
    }
}
