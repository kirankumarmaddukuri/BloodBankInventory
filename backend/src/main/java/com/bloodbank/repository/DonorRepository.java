package com.bloodbank.repository;

import com.bloodbank.entity.Donor;
import com.bloodbank.entity.enums.BloodGroup;
import com.bloodbank.entity.enums.EligibilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {
    List<Donor> findByBloodGroupAndEligibilityStatus(BloodGroup bloodGroup, EligibilityStatus eligibilityStatus);
    Optional<Donor> findByUserId(Long userId);
}
