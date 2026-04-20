package com.bloodbank.controller;

import com.bloodbank.entity.User;
import com.bloodbank.entity.enums.Role;
import com.bloodbank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // POST /api/users is handled by AuthController /api/auth/register

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping(params = "role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsersByRole(@RequestParam Role role) {
        return ResponseEntity.ok(userRepository.findByRole(role));
    }
}
