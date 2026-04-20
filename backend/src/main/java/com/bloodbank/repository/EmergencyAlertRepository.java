package com.bloodbank.repository;

import com.bloodbank.entity.EmergencyAlert;
import com.bloodbank.entity.enums.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyAlertRepository extends JpaRepository<EmergencyAlert, Long> {
    List<EmergencyAlert> findByStatus(AlertStatus status);
}
