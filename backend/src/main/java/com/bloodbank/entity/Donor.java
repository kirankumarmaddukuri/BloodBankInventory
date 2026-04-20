package com.bloodbank.entity;

import com.bloodbank.entity.enums.BloodGroup;
import com.bloodbank.entity.enums.EligibilityStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "donors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BloodGroup bloodGroup;

    @Column(nullable = false)
    private Double weight;

    private Double hemoglobin;

    private LocalDate lastDonationDate;

    @Column(nullable = false)
    private Integer totalDonations = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EligibilityStatus eligibilityStatus;

    @Column(columnDefinition = "TEXT")
    private String medicalHistory;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime registeredAt;

}
