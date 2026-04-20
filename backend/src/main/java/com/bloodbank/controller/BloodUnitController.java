package com.bloodbank.controller;

import com.bloodbank.dto.CollectBloodRequest;
import com.bloodbank.dto.TestingResultRequest;
import com.bloodbank.entity.BloodUnit;
import com.bloodbank.entity.enums.UnitStatus;
import com.bloodbank.repository.BloodUnitRepository;
import com.bloodbank.service.BloodUnitService;
import com.bloodbank.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/blood-units")
@RequiredArgsConstructor
public class BloodUnitController {

    private final BloodUnitService bloodUnitService;
    private final BloodUnitRepository bloodUnitRepository;

    @PostMapping
    @PreAuthorize("hasRole('BLOOD_BANK_ADMIN')")
    public ResponseEntity<BloodUnit> collectBlood(@Valid @RequestBody CollectBloodRequest request) {
        return ResponseEntity.ok(bloodUnitService.collectBlood(
                request.getDonorId(), 
                request.getComponentType(), 
                request.getVolumeML(),
                request.getDonorWeight(),
                request.getDonorHemoglobin()
        ));
    }

    @PutMapping("/{id}/test")
    @PreAuthorize("hasRole('BLOOD_BANK_ADMIN')")
    public ResponseEntity<BloodUnit> recordTestingResult(@PathVariable Long id, @RequestBody TestingResultRequest request) {
        return ResponseEntity.ok(bloodUnitService.recordTestingResult(id, request.isTestedForHiv(), request.isTestedForHepatitis(), request.isTestedForMalaria(), request.isTestedForSyphilis()));
    }

    @PutMapping("/{id}/discard")
    @PreAuthorize("hasRole('BLOOD_BANK_ADMIN')")
    public ResponseEntity<BloodUnit> discardUnit(@PathVariable Long id) {
        return ResponseEntity.ok(bloodUnitService.discardUnit(id));
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAnyRole('ADMIN', 'BLOOD_BANK_ADMIN', 'EMERGENCY_COORDINATOR', 'HOSPITAL_STAFF')")
    public ResponseEntity<List<BloodUnit>> getInventory() {
        return ResponseEntity.ok(bloodUnitRepository.findByStatus(UnitStatus.AVAILABLE));
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('ADMIN', 'BLOOD_BANK_ADMIN')")
    public ResponseEntity<List<BloodUnit>> getExpiringUnits() {
        // Fetch units expiring within 7 days
        LocalDate limitDate = LocalDate.now().plusDays(7);
        return ResponseEntity.ok(bloodUnitRepository.findByExpiryDateBeforeAndStatus(limitDate, UnitStatus.AVAILABLE));
    }

    @GetMapping("/testing")
    @PreAuthorize("hasAnyRole('ADMIN', 'BLOOD_BANK_ADMIN')")
    public ResponseEntity<List<BloodUnit>> getTestingUnits() {
        List<BloodUnit> all = bloodUnitRepository.findAll();
        List<BloodUnit> testing = new java.util.ArrayList<>();
        for(BloodUnit b : all) {
            if(b.getStatus() == UnitStatus.COLLECTED || b.getStatus() == UnitStatus.TESTING) {
                testing.add(b);
            }
        }
        return ResponseEntity.ok(testing);
    }
}
