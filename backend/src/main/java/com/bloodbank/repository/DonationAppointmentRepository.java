package com.bloodbank.repository;

import com.bloodbank.entity.DonationAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationAppointmentRepository extends JpaRepository<DonationAppointment, Long> {
    List<DonationAppointment> findByDonorId(Long donorId);
}
