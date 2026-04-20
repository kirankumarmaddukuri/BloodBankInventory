package com.bloodbank.service;

import com.bloodbank.entity.BloodRequest;
import com.bloodbank.entity.BloodUnit;
import com.bloodbank.entity.User;
import com.bloodbank.entity.enums.*;
import com.bloodbank.repository.BloodRequestRepository;
import com.bloodbank.repository.BloodUnitRepository;
import com.bloodbank.util.CompatibilityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BloodRequestService {

    private final BloodRequestRepository requestRepository;
    private final BloodUnitRepository unitRepository;
    private final InventoryService inventoryService;

    public BloodRequest createRequest(String hospital, String patientName, BloodGroup bloodGroup, ComponentType componentType, int units, RequestPriority priority) {
        BloodRequest request = BloodRequest.builder()
                .requestingHospital(hospital)
                .patientName(patientName)
                .patientBloodGroup(bloodGroup)
                .componentType(componentType)
                .unitsRequested(units)
                .priority(priority)
                .status(RequestStatus.PENDING)
                .build();
        return requestRepository.save(request);
    }

    public BloodRequest processRequest(Long requestId) {
        BloodRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Request is already being processed or fulfilled");
        }
        
        request.setStatus(RequestStatus.PROCESSING);
        return requestRepository.save(request);
    }

    public BloodRequest fulfillRequest(Long requestId, User admin) {
        BloodRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != RequestStatus.PROCESSING) {
            throw new RuntimeException("Request must be in PROCESSING state to fulfill");
        }

        List<BloodGroup> compatibleGroups = CompatibilityUtil.getCompatibleDonorGroups(request.getPatientBloodGroup());
        List<BloodUnit> availableUnits = unitRepository.findByStatus(UnitStatus.AVAILABLE);

        List<BloodUnit> unitsToAllocate = new ArrayList<>();
        
        for (BloodUnit unit : availableUnits) {
            if (unit.getComponentType() == request.getComponentType() && compatibleGroups.contains(unit.getBloodGroup())) {
                unitsToAllocate.add(unit);
                if (unitsToAllocate.size() == request.getUnitsRequested()) {
                    break;
                }
            }
        }

        if (unitsToAllocate.size() < request.getUnitsRequested()) {
            throw new RuntimeException("Not enough compatible units available in inventory. Available: " + unitsToAllocate.size());
        }

        for (BloodUnit unit : unitsToAllocate) {
            unit.setStatus(UnitStatus.ISSUED);
            unitRepository.save(unit);
            inventoryService.logTransaction(unit.getBloodGroup(), unit.getComponentType(), 0, 1, TransactionType.REQUEST_FULFILLMENT, request.getId());
        }

        request.setStatus(RequestStatus.FULFILLED);
        request.setFulfilledAt(LocalDateTime.now());
        request.setFulfilledBy(admin);

        return requestRepository.save(request);
    }
}
