package com.bloodbank.dto;

import com.bloodbank.entity.enums.BloodGroup;
import com.bloodbank.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String password;

    @NotNull
    private Role role;

    private BloodGroup bloodGroup;

    private java.time.LocalDate dateOfBirth;
}
