package com.bloodbank.service;

import com.bloodbank.entity.Donor;
import com.bloodbank.entity.DonorNotification;
import com.bloodbank.entity.EmergencyAlert;
import com.bloodbank.entity.User;
import com.bloodbank.entity.enums.*;
import com.bloodbank.repository.DonorNotificationRepository;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.repository.EmergencyAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmergencyMatchingService {

    private final EmergencyAlertRepository emergencyAlertRepository;
    private final DonorRepository donorRepository;
    private final DonorNotificationRepository donorNotificationRepository;

    public EmergencyAlert triggerEmergency(User coordinator, BloodGroup requiredGroup, ComponentType componentType, int unitsNeeded, String location, UrgencyLevel urgency) {
        EmergencyAlert alert = EmergencyAlert.builder()
                .triggeredBy(coordinator)
                .requiredBloodGroup(requiredGroup)
                .requiredComponentType(componentType)
                .unitsNeeded(unitsNeeded)
                .location(location)
                .urgency(urgency)
                .status(AlertStatus.ACTIVE)
                .build();
        
        alert = emergencyAlertRepository.save(alert);
        
        matchAndNotifyDonors(alert);
        return alert;
    }

    private void matchAndNotifyDonors(EmergencyAlert alert) {
        List<Donor> allDonors = donorRepository.findAll();

        for (Donor donor : allDonors) {
            if (donor.getEligibilityStatus() == EligibilityStatus.ELIGIBLE 
                    && donor.getBloodGroup() == alert.getRequiredBloodGroup()) {

                DonorNotification notification = DonorNotification.builder()
                        .emergencyAlert(alert)
                        .donor(donor)
                        .notificationChannel(NotificationChannel.SMS) // default for emergency
                        .responseStatus(ResponseStatus.PENDING)
                        .build();

                donorNotificationRepository.save(notification);
                
                // Real-world integration: Code to send SMS would go here.
            }
        }
    }

    public EmergencyAlert fulfillAlert(Long alertId) {
        EmergencyAlert alert = emergencyAlertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        alert.setStatus(AlertStatus.FULFILLED);
        alert.setResolvedAt(LocalDateTime.now());
        return emergencyAlertRepository.save(alert);
    }
}
