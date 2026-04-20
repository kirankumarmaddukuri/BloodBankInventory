package com.bloodbank.controller;

import com.bloodbank.dto.TransfusionLogRequest;
import com.bloodbank.entity.BloodRequest;
import com.bloodbank.entity.BloodUnit;
import com.bloodbank.entity.TransfusionRecord;
import com.bloodbank.repository.BloodRequestRepository;
import com.bloodbank.repository.BloodUnitRepository;
import com.bloodbank.repository.TransfusionRecordRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfusions")
@RequiredArgsConstructor
public class TransfusionController {

    private final TransfusionRecordRepository transfusionRecordRepository;
    private final BloodUnitRepository bloodUnitRepository;
    private final BloodRequestRepository bloodRequestRepository;

    @PostMapping
    @PreAuthorize("hasRole('HOSPITAL_STAFF')")
    public ResponseEntity<TransfusionRecord> logTransfusion(@Valid @RequestBody TransfusionLogRequest request) {
        BloodUnit unit = bloodUnitRepository.findById(request.getBloodUnitId())
                .orElseThrow(() -> new RuntimeException("Blood unit not found"));
        
        BloodRequest bloodRequest = bloodRequestRepository.findById(request.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        TransfusionRecord record = TransfusionRecord.builder()
                .bloodUnit(unit)
                .request(bloodRequest)
                .patientName(request.getPatientName())
                .hospital(request.getHospital())
                .transfusedBy(request.getTransfusedBy())
                .adverseReaction(request.isAdverseReaction())
                .reactionRemarks(request.getReactionRemarks())
                .build();
                
        return ResponseEntity.ok(transfusionRecordRepository.save(record));
    }

    @GetMapping("/blood-unit/{unitId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BLOOD_BANK_ADMIN', 'HOSPITAL_STAFF')")
    public ResponseEntity<List<TransfusionRecord>> getTransfusionsByUnitId(@PathVariable Long unitId) {
        return ResponseEntity.ok(transfusionRecordRepository.findByBloodUnitId(unitId));
    }
}
