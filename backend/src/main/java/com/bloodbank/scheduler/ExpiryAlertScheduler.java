package com.bloodbank.scheduler;

import com.bloodbank.entity.BloodUnit;
import com.bloodbank.entity.enums.TransactionType;
import com.bloodbank.entity.enums.UnitStatus;
import com.bloodbank.repository.BloodUnitRepository;
import com.bloodbank.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpiryAlertScheduler {

    private final BloodUnitRepository bloodUnitRepository;
    private final InventoryService inventoryService;

    // Run every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void processExpiredUnits() {
        log.info("Running daily scheduled task: Expiry Checker");
        
        LocalDate today = LocalDate.now();
        List<BloodUnit> expiredUnits = bloodUnitRepository.findByExpiryDateBeforeAndStatus(today, UnitStatus.AVAILABLE);

        for (BloodUnit unit : expiredUnits) {
            unit.setStatus(UnitStatus.EXPIRED);
            bloodUnitRepository.save(unit);
            inventoryService.logTransaction(
                    unit.getBloodGroup(), 
                    unit.getComponentType(), 
                    0, 
                    1, 
                    TransactionType.EXPIRY, 
                    unit.getId()
            );
            log.info("Marked Blood Unit {} as EXPIRED. Triggered inventory deduction.", unit.getUnitNumber());
        }

        // Warning alerts for 7 days
        LocalDate warningDate = today.plusDays(7);
        List<BloodUnit> expiringSoon = bloodUnitRepository.findByExpiryDateBeforeAndStatus(warningDate, UnitStatus.AVAILABLE);
        if (!expiringSoon.isEmpty()) {
            log.warn("WARNING: {} blood units are expiring within the next 7 days!", expiringSoon.size());
        }
    }
}
