package com.bloodbank.repository;

import com.bloodbank.entity.DonorNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonorNotificationRepository extends JpaRepository<DonorNotification, Long> {
    List<DonorNotification> findByEmergencyAlertId(Long emergencyAlertId);
    List<DonorNotification> findByDonorId(Long donorId);
}
