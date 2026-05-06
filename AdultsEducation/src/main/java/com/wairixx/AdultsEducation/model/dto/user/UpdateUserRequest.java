package com.wairixx.AdultsEducation.model.dto.user;

import com.wairixx.AdultsEducation.model.enums.Role;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record UpdateUserRequest(
        @Email String email,
        @Size(max = 50) String firstName,
        @Size(max = 50) String lastName,
        @Size(max = 20) String phone,
        @Size(max = 2000) String bio,
        @Size(max = 500) String avatarUrl,
        LocalDate birthDate,
        @Size(max = 100) String specialization,
        @PositiveOrZero Double experienceYears,
        Role role,
        Boolean active) {}