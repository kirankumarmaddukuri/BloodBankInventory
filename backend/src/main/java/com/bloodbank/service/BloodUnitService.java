package com.bloodbank.service;

import com.bloodbank.entity.BloodUnit;
import com.bloodbank.entity.Donor;
import com.bloodbank.entity.enums.ComponentType;
import com.bloodbank.entity.enums.TransactionType;
import com.bloodbank.entity.enums.UnitStatus;
import com.bloodbank.repository.BloodUnitRepository;
import com.bloodbank.repository.DonorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BloodUnitService {

    private final BloodUnitRepository bloodUnitRepository;
    private final DonorRepository donorRepository;
    private final InventoryService inventoryService;

    public BloodUnit collectBlood(Long donorId, ComponentType componentType, Integer volume, Double weight, Double hemoglobin) {
        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        if (!"ELIGIBLE".equals(donor.getEligibilityStatus().name())) {
            throw new RuntimeException("Donor is not eligible to donate");
        }

        if (weight != null && weight < 50.0) {
            throw new RuntimeException("Minimum donor weight is 50 kg");
        }
        if (hemoglobin != null && hemoglobin < 12.5) {
            throw new RuntimeException("Minimum hemoglobin is 12.5 g/dL");
        }
        if (donor.getLastDonationDate() != null) {
            long daysSince = java.time.temporal.ChronoUnit.DAYS.between(donor.getLastDonationDate(), LocalDate.now());
            if (daysSince < 90) {
                throw new RuntimeException("Donation interval of 90 days not fulfilled (Days passed: " + daysSince + ")");
            }
        }
        
        donor.setWeight(weight);
        donor.setHemoglobin(hemoglobin);

        LocalDate collectionDate = LocalDate.now();
        LocalDate expiryDate = calculateExpiryDate(componentType, collectionDate);

        BloodUnit unit = BloodUnit.builder()
                .unitNumber(UUID.randomUUID().toString()) // Generated Unique ID
                .donor(donor)
                .bloodGroup(donor.getBloodGroup())
                .componentType(componentType)
                .volumeML(volume == null ? 350 : volume)
                .collectionDate(collectionDate)
                .expiryDate(expiryDate)
                .status(UnitStatus.COLLECTED)
                .build();

        donor.setTotalDonations(donor.getTotalDonations() + 1);
        donor.setLastDonationDate(collectionDate);
        donorRepository.save(donor);

        return bloodUnitRepository.save(unit);
    }

    public BloodUnit recordTestingResult(Long unitId, boolean hiv, boolean hepatitis, boolean malaria, boolean syphilis) {
        BloodUnit unit = bloodUnitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Blood unit not found"));

        if (unit.getStatus() != UnitStatus.COLLECTED && unit.getStatus() != UnitStatus.TESTING) {
            throw new RuntimeException("Unit must be in COLLECTED or TESTING status");
        }

        unit.setTestedForHiv(hiv);
        unit.setTestedForHepatitis(hepatitis);
        unit.setTestedForMalaria(malaria);
        unit.setTestedForSyphilis(syphilis);

        if (hiv || hepatitis || malaria || syphilis) {
            unit.setStatus(UnitStatus.DISCARDED);
            inventoryService.logTransaction(unit.getBloodGroup(), unit.getComponentType(), 0, 1, TransactionType.DISCARD, unit.getId());
        } else {
            unit.setStatus(UnitStatus.AVAILABLE);
            // Log as donation added to inventory since it's now available
            inventoryService.logTransaction(unit.getBloodGroup(), unit.getComponentType(), 1, 0, TransactionType.DONATION, unit.getId());
        }

        return bloodUnitRepository.save(unit);
    }

    public BloodUnit discardUnit(Long unitId) {
        BloodUnit unit = bloodUnitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        if (unit.getStatus() == UnitStatus.AVAILABLE || unit.getStatus() == UnitStatus.EXPIRED) {
            unit.setStatus(UnitStatus.DISCARDED);
            inventoryService.logTransaction(unit.getBloodGroup(), unit.getComponentType(), 0, 1, TransactionType.DISCARD, unit.getId());
            return bloodUnitRepository.save(unit);
        } else {
            throw new RuntimeException("Unit cannot be discarded from its current status");
        }
    }

    private LocalDate calculateExpiryDate(ComponentType type, LocalDate collectionDate) {
        switch (type) {
            case PLATELETS:
                return collectionDate.plusDays(5);
            case RBC:
                return collectionDate.plusDays(42); // standard
            case WHOLE_BLOOD:
            default:
                return collectionDate.plusDays(35); // as per requirements
        }
    }
}
