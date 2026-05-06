package com.wairixx.AdultsEducation.model.dto.user;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateMeRequest(
        @Size(max = 50) String firstName,
        @Size(max = 50) String lastName,
        @Size(max = 20) String phone,
        @Size(max = 2000) String bio,
        @Size(max = 500) String avatarUrl,
        LocalDate birthDate,
        // лише для викладачів (ігноруються для студентів)
        @Size(max = 100) String specialization,
        @PositiveOrZero Double experienceYears) {}