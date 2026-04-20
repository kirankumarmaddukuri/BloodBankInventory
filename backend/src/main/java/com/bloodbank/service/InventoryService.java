package com.bloodbank.service;

import com.bloodbank.entity.InventoryLog;
import com.bloodbank.entity.enums.BloodGroup;
import com.bloodbank.entity.enums.ComponentType;
import com.bloodbank.entity.enums.TransactionType;
import com.bloodbank.entity.enums.UnitStatus;
import com.bloodbank.repository.BloodUnitRepository;
import com.bloodbank.repository.InventoryLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final BloodUnitRepository bloodUnitRepository;
    private final InventoryLogRepository inventoryLogRepository;

    public void logTransaction(BloodGroup bloodGroup, ComponentType componentType, 
                               int added, int removed, TransactionType type, Long referenceId) {
        InventoryLog log = InventoryLog.builder()
                .bloodBank("Main Regional Bank") // Should ideally come from config or user
                .bloodGroup(bloodGroup)
                .componentType(componentType)
                .unitsAdded(added)
                .unitsRemoved(removed)
                .transactionType(type)
                .referenceId(referenceId)
                .build();
        inventoryLogRepository.save(log);
        
        checkAlertThreshold(bloodGroup, componentType);
    }

    public long getCurrentStock(BloodGroup bloodGroup, ComponentType componentType) {
        return bloodUnitRepository.findByBloodGroupAndComponentTypeAndStatus(bloodGroup, componentType, UnitStatus.AVAILABLE).size();
    }

    private void checkAlertThreshold(BloodGroup bloodGroup, ComponentType componentType) {
        long currentStock = getCurrentStock(bloodGroup, componentType);
        if (currentStock < 5) {
            System.out.println("CRITICAL ALERT: Stock for " + bloodGroup + " " + componentType + " is critically low! Current stock: " + currentStock);
        } else if (currentStock < 10) {
            System.out.println("WARNING: Stock for " + bloodGroup + " " + componentType + " is low! Current stock: " + currentStock);
        }
    }
}
