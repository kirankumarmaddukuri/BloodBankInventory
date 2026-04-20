package com.bloodbank.entity;

import com.bloodbank.entity.enums.BloodGroup;
import com.bloodbank.entity.enums.ComponentType;
import com.bloodbank.entity.enums.UnitStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "blood_units")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String unitNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BloodGroup bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComponentType componentType;

    @Column(nullable = false)
    private Integer volumeML = 350;

    @Column(nullable = false)
    private LocalDate collectionDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitStatus status;

    private String storageLocation;

    // A simple CSV or JSON string for tests, or separate boolean fields
    private Boolean testedForHiv = false;
    private Boolean testedForHepatitis = false;
    private Boolean testedForMalaria = false;
    private Boolean testedForSyphilis = false;
    
}
