package com.bloodbank.service;

import com.bloodbank.dto.AuthRequest;
import com.bloodbank.dto.AuthResponse;
import com.bloodbank.dto.RegisterRequest;
import com.bloodbank.entity.Donor;
import com.bloodbank.entity.User;
import com.bloodbank.entity.enums.EligibilityStatus;
import com.bloodbank.entity.enums.Role;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.repository.UserRepository;
import com.bloodbank.security.CustomUserDetails;
import com.bloodbank.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DonorRepository donorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        if (request.getRole() == Role.DONOR) {
             if (request.getDateOfBirth() == null) {
                  throw new RuntimeException("Date of birth is legally required for donors.");
             }
             int age = java.time.Period.between(request.getDateOfBirth(), java.time.LocalDate.now()).getYears();
             if (age < 18 || age > 65) {
                  throw new RuntimeException("Donor age must strictly be between 18 and 65 years.");
             }
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .bloodGroup(request.getRole() == Role.DONOR ? request.getBloodGroup() : null)
                .dateOfBirth(request.getDateOfBirth())
                .build();

        user = userRepository.save(user);

        if (request.getRole() == Role.DONOR) {
            if (request.getBloodGroup() == null) {
                throw new RuntimeException("Blood group is required for DONOR registration");
            }
            Donor donor = Donor.builder()
                    .user(user)
                    .bloodGroup(request.getBloodGroup())
                    .weight(0.0) // Expected to be updated during donation screening
                    .totalDonations(0)
                    .eligibilityStatus(EligibilityStatus.ELIGIBLE)
                    .build();
            donorRepository.save(donor);
        }

        var jwtToken = jwtService.generateToken(new CustomUserDetails(user));

        return AuthResponse.builder()
                .token(jwtToken)
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var jwtToken = jwtService.generateToken(new CustomUserDetails(user));

        return AuthResponse.builder()
                .token(jwtToken)
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
