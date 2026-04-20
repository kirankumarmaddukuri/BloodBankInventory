package com.bloodbank.entity;

import com.bloodbank.entity.enums.AlertStatus;
import com.bloodbank.entity.enums.BloodGroup;
import com.bloodbank.entity.enums.ComponentType;
import com.bloodbank.entity.enums.UrgencyLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "triggered_by_user_id", nullable = false)
    private User triggeredBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BloodGroup requiredBloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComponentType requiredComponentType;

    @Column(nullable = false)
    private Integer unitsNeeded;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UrgencyLevel urgency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime triggeredAt;

    private LocalDateTime resolvedAt;
}
