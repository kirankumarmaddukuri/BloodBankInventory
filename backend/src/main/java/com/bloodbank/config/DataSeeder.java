package com.bloodbank.config;

import com.bloodbank.entity.*;
import com.bloodbank.entity.enums.*;
import com.bloodbank.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DonorRepository donorRepository;
    private final BloodUnitRepository bloodUnitRepository;
    private final BloodRequestRepository requestRepository;
    private final DonationAppointmentRepository appointmentRepository;
    private final TransfusionRecordRepository transfusionRepository;
    private final EmergencyAlertRepository emergencyAlertRepository;
    private final DonorNotificationRepository donorNotificationRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) return;

        

        // ─────────────────────────────────────────────────────────────────
        // 1. USERS
        // ─────────────────────────────────────────────────────────────────
        User admin = userRepository.save(User.builder()
                .name("Super Admin").email("admin@gmail.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN).phone("9000000001").build());

        User bankAdmin = userRepository.save(User.builder()
                .name("Ravi Kumar").email("manager@gmail.com")
                .password(passwordEncoder.encode("manager123"))
                .role(Role.BLOOD_BANK_ADMIN).phone("9000000002").build());

        User hospitalStaff = userRepository.save(User.builder()
                .name("Dr. Priya Sharma").email("hospital@gmail.com")
                .password(passwordEncoder.encode("hospital123"))
                .role(Role.HOSPITAL_STAFF).phone("9000000003").build());

        User donorUser1 = userRepository.save(User.builder()
                .name("John Doe").email("donor@gmail.com")
                .password(passwordEncoder.encode("donor123"))
                .role(Role.DONOR).phone("9000000004")
                .dateOfBirth(LocalDate.of(1990, 5, 10)).build());

        User donorUser2 = userRepository.save(User.builder()
                .name("Ananya Patel").email("ananya@gmail.com")
                .password(passwordEncoder.encode("donor123"))
                .role(Role.DONOR).phone("9000000005")
                .dateOfBirth(LocalDate.of(1995, 8, 22)).build());

        User donorUser3 = userRepository.save(User.builder()
                .name("Carlos Mendes").email("carlos@gmail.com")
                .password(passwordEncoder.encode("donor123"))
                .role(Role.DONOR).phone("9000000006")
                .dateOfBirth(LocalDate.of(1988, 3, 15)).build());

        User dispatcher = userRepository.save(User.builder()
                .name("Emergency Dispatch").email("dispatcher@gmail.com")
                .password(passwordEncoder.encode("dispatcher123"))
                .role(Role.EMERGENCY_COORDINATOR).phone("9000000007").build());

        // ─────────────────────────────────────────────────────────────────
        // 2. DONORS
        // ─────────────────────────────────────────────────────────────────
        Donor donor1 = donorRepository.save(Donor.builder()
                .user(donorUser1).bloodGroup(BloodGroup.O_NEG)
                .eligibilityStatus(EligibilityStatus.ELIGIBLE)
                .totalDonations(5).weight(75.5).hemoglobin(14.2)
                .lastDonationDate(LocalDate.now().minusDays(100)).build());

        Donor donor2 = donorRepository.save(Donor.builder()
                .user(donorUser2).bloodGroup(BloodGroup.A_POS)
                .eligibilityStatus(EligibilityStatus.ELIGIBLE)
                .totalDonations(3).weight(62.0).hemoglobin(13.5)
                .lastDonationDate(LocalDate.now().minusDays(95)).build());

        Donor donor3 = donorRepository.save(Donor.builder()
                .user(donorUser3).bloodGroup(BloodGroup.B_NEG)
                .eligibilityStatus(EligibilityStatus.ELIGIBLE)
                .totalDonations(2).weight(80.0).hemoglobin(13.0)
                .lastDonationDate(LocalDate.now().minusDays(100)).build());

        // ─────────────────────────────────────────────────────────────────
        // 3. DONATION APPOINTMENTS
        // ─────────────────────────────────────────────────────────────────
        appointmentRepository.save(DonationAppointment.builder()
                .donor(donor1).appointmentDate(LocalDateTime.now().plusDays(3))
                .status(AppointmentStatus.SCHEDULED).notes("Morning slot preferred").build());

        appointmentRepository.save(DonationAppointment.builder()
                .donor(donor2).appointmentDate(LocalDateTime.now().plusDays(7))
                .status(AppointmentStatus.SCHEDULED).notes("After 10AM").build());

        appointmentRepository.save(DonationAppointment.builder()
                .donor(donor1).appointmentDate(LocalDateTime.now().minusDays(100))
                .status(AppointmentStatus.COMPLETED).notes("Whole blood collection, no complications.").build());

        // ─────────────────────────────────────────────────────────────────
        // 4. BLOOD UNITS
        // ─────────────────────────────────────────────────────────────────
        BloodUnit unit1 = bloodUnitRepository.save(BloodUnit.builder()
                .unitNumber("BU-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                .donor(donor1).bloodGroup(BloodGroup.O_NEG)
                .componentType(ComponentType.WHOLE_BLOOD).volumeML(350)
                .collectionDate(LocalDate.now().minusDays(5))
                .expiryDate(LocalDate.now().plusDays(30))
                .status(UnitStatus.AVAILABLE).storageLocation("Rack A-1").build());

        BloodUnit unit2 = bloodUnitRepository.save(BloodUnit.builder()
                .unitNumber("BU-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                .donor(donor1).bloodGroup(BloodGroup.O_NEG)
                .componentType(ComponentType.RBC).volumeML(250)
                .collectionDate(LocalDate.now().minusDays(3))
                .expiryDate(LocalDate.now().plusDays(40))
                .status(UnitStatus.AVAILABLE).storageLocation("Rack B-2").build());

        BloodUnit unit3 = bloodUnitRepository.save(BloodUnit.builder()
                .unitNumber("BU-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                .donor(donor2).bloodGroup(BloodGroup.A_POS)
                .componentType(ComponentType.PLATELETS).volumeML(200)
                .collectionDate(LocalDate.now().minusDays(1))
                .expiryDate(LocalDate.now().plusDays(5))  // Expiring soon!
                .status(UnitStatus.AVAILABLE).storageLocation("Rack C-3").build());

        BloodUnit unit4 = bloodUnitRepository.save(BloodUnit.builder()
                .unitNumber("BU-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                .donor(donor2).bloodGroup(BloodGroup.A_POS)
                .componentType(ComponentType.PLASMA).volumeML(400)
                .collectionDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(60))
                .status(UnitStatus.AVAILABLE).storageLocation("Rack D-1").build());

        BloodUnit unit5 = bloodUnitRepository.save(BloodUnit.builder()
                .unitNumber("BU-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                .donor(donor1).bloodGroup(BloodGroup.O_NEG)
                .componentType(ComponentType.WHOLE_BLOOD).volumeML(350)
                .collectionDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(4))  // Expiring soon!
                .status(UnitStatus.TESTING).storageLocation(null).build());

        BloodUnit unit6 = bloodUnitRepository.save(BloodUnit.builder()
                .unitNumber("BU-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                .donor(donor3).bloodGroup(BloodGroup.B_NEG)
                .componentType(ComponentType.RBC).volumeML(300)
                .collectionDate(LocalDate.now().minusDays(10))
                .expiryDate(LocalDate.now().plusDays(25))
                .status(UnitStatus.AVAILABLE).storageLocation("Rack E-2").build());

        // ─────────────────────────────────────────────────────────────────
        // 5. BLOOD REQUESTS
        // ─────────────────────────────────────────────────────────────────
        BloodRequest req1 = requestRepository.save(BloodRequest.builder()
                .requestingHospital("Apollo Hospital, Chennai")
                .patientName("Alice Fernandez")
                .patientBloodGroup(BloodGroup.O_NEG)
                .componentType(ComponentType.WHOLE_BLOOD)
                .unitsRequested(1).priority(RequestPriority.URGENT)
                .status(RequestStatus.PENDING).build());

        BloodRequest req2 = requestRepository.save(BloodRequest.builder()
                .requestingHospital("AIIMS, Delhi")
                .patientName("Ramesh Gupta")
                .patientBloodGroup(BloodGroup.A_POS)
                .componentType(ComponentType.PLATELETS)
                .unitsRequested(2).priority(RequestPriority.ROUTINE)
                .status(RequestStatus.PENDING).build());

        BloodRequest req3 = requestRepository.save(BloodRequest.builder()
                .requestingHospital("Fortis Hospital, Bangalore")
                .patientName("Sunita Mehta")
                .patientBloodGroup(BloodGroup.B_NEG)
                .componentType(ComponentType.RBC)
                .unitsRequested(1).priority(RequestPriority.EMERGENCY)
                .status(RequestStatus.PROCESSING).build());

        BloodRequest req4 = requestRepository.save(BloodRequest.builder()
                .requestingHospital("Manipal Hospital, Pune")
                .patientName("David Raj")
                .patientBloodGroup(BloodGroup.A_POS)
                .componentType(ComponentType.PLASMA)
                .unitsRequested(1).priority(RequestPriority.ROUTINE)
                .status(RequestStatus.FULFILLED)
                .fulfilledAt(LocalDateTime.now().minusDays(1))
                .fulfilledBy(bankAdmin).build());

        // ─────────────────────────────────────────────────────────────────
        // 6. TRANSFUSION RECORDS
        // ─────────────────────────────────────────────────────────────────
        transfusionRepository.save(TransfusionRecord.builder()
                .bloodUnit(unit4).request(req4)
                .patientName("David Raj")
                .hospital("Manipal Hospital, Pune")
                .transfusedBy("Dr. Priya Sharma")
                .adverseReaction(false)
                .reactionRemarks("No adverse effects observed. Patient stable.")
                .build());

        // ─────────────────────────────────────────────────────────────────
        // 7. EMERGENCY ALERTS
        // ─────────────────────────────────────────────────────────────────
        EmergencyAlert alert1 = emergencyAlertRepository.save(EmergencyAlert.builder()
                .triggeredBy(dispatcher)
                .requiredBloodGroup(BloodGroup.O_NEG)
                .requiredComponentType(ComponentType.WHOLE_BLOOD)
                .unitsNeeded(4)
                .location("Mass Casualty - NH-48 Accident, Chennai")
                .urgency(UrgencyLevel.CRITICAL)
                .status(AlertStatus.ACTIVE)
                .build());

        // ─────────────────────────────────────────────────────────────────
        // 8. DONOR NOTIFICATIONS (Emergency Matching)
        // ─────────────────────────────────────────────────────────────────
        donorNotificationRepository.save(DonorNotification.builder()
                .emergencyAlert(alert1).donor(donor1)
                .notificationChannel(NotificationChannel.SMS)
                .responseStatus(ResponseStatus.PENDING).build());

        // ─────────────────────────────────────────────────────────────────
        // 9. INVENTORY LOGS
        // ─────────────────────────────────────────────────────────────────
        inventoryLogRepository.save(InventoryLog.builder()
                .bloodBank("Central Blood Bank").bloodGroup(BloodGroup.O_NEG)
                .componentType(ComponentType.WHOLE_BLOOD)
                .unitsAdded(1).unitsRemoved(0)
                .transactionType(TransactionType.DONATION)
                .referenceId(unit1.getId()).build());

        inventoryLogRepository.save(InventoryLog.builder()
                .bloodBank("Central Blood Bank").bloodGroup(BloodGroup.A_POS)
                .componentType(ComponentType.PLASMA)
                .unitsAdded(0).unitsRemoved(1)
                .transactionType(TransactionType.REQUEST_FULFILLMENT)
                .referenceId(req4.getId()).build());

        System.out.println(" ALL TABLES SEEDED SUCCESSFULLY! READY FOR DEMO! ");
        System.out.println("  admin@gmail.com       / admin123");
        System.out.println("  manager@gmail.com     / manager123");
        System.out.println("  hospital@gmail.com    / hospital123");
        System.out.println("  donor@gmail.com       / donor123");
        System.out.println("  dispatcher@gmail.com  / dispatcher123");
    }
}
