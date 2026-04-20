package com.bloodbank.controller;

import com.bloodbank.dto.AppointmentRequest;
import com.bloodbank.entity.DonationAppointment;
import com.bloodbank.entity.Donor;
import com.bloodbank.entity.User;
import com.bloodbank.entity.enums.AppointmentStatus;
import com.bloodbank.repository.DonationAppointmentRepository;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final DonationAppointmentRepository appointmentRepository;
    private final DonorRepository donorRepository;

    @PostMapping
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<DonationAppointment> scheduleAppointment(
            @Valid @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal CustomUserDetails currUser) {

        Donor donor = donorRepository.findByUserId(currUser.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Donor profile not found"));

        DonationAppointment appt = DonationAppointment.builder()
                .donor(donor)
                .appointmentDate(request.getAppointmentDate())
                .notes(request.getNotes())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        return ResponseEntity.ok(appointmentRepository.save(appt));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<List<DonationAppointment>> getMyAppointments(@AuthenticationPrincipal CustomUserDetails currUser) {
        Donor donor = donorRepository.findByUserId(currUser.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Donor profile not found"));
        return ResponseEntity.ok(appointmentRepository.findByDonorId(donor.getId()));
    }
}
