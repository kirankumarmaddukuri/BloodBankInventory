package com.bloodbank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transfusion_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransfusionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blood_unit_id", nullable = false)
    private BloodUnit bloodUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blood_request_id", nullable = false)
    private BloodRequest request;

    private String patientName;
    
    private String hospital;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime transfusedAt;

    @Column(nullable = false)
    private String transfusedBy;

    @Column(nullable = false)
    private Boolean adverseReaction;

    @Column(columnDefinition = "TEXT")
    private String reactionRemarks;
}
