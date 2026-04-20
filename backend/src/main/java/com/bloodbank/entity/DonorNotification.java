package com.bloodbank.entity;

import com.bloodbank.entity.enums.NotificationChannel;
import com.bloodbank.entity.enums.ResponseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "donor_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonorNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emergency_alert_id", nullable = false)
    private EmergencyAlert emergencyAlert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel notificationChannel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResponseStatus responseStatus;

    private LocalDateTime respondedAt;
}
