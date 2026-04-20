package com.bloodbank.repository;

import com.bloodbank.entity.BloodUnit;
import com.bloodbank.entity.enums.BloodGroup;
import com.bloodbank.entity.enums.ComponentType;
import com.bloodbank.entity.enums.UnitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BloodUnitRepository extends JpaRepository<BloodUnit, Long> {
    List<BloodUnit> findByStatus(UnitStatus status);
    List<BloodUnit> findByBloodGroupAndComponentTypeAndStatus(BloodGroup bloodGroup, ComponentType componentType, UnitStatus status);
    List<BloodUnit> findByExpiryDateBeforeAndStatus(LocalDate date, UnitStatus status);
    List<BloodUnit> findByDonorId(Long donorId);
    List<BloodUnit> findByStatusIn(List<UnitStatus> statuses);
}
