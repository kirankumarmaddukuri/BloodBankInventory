package com.bloodbank.entity;

import com.bloodbank.entity.enums.BloodGroup;
import com.bloodbank.entity.enums.ComponentType;
import com.bloodbank.entity.enums.RequestPriority;
import com.bloodbank.entity.enums.RequestStatus;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blood_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String requestingHospital;

    private String patientName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BloodGroup patientBloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComponentType componentType;

    @Column(nullable = false)
    private Integer unitsRequested;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime fulfilledAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fulfilled_by_user_id")
    @JsonIgnoreProperties({"password", "phone", "dateOfBirth", "createdAt"})
    private User fulfilledBy;

}
