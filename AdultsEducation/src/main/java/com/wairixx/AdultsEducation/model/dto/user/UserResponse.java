package com.wairixx.AdultsEducation.model.dto.user;

import com.wairixx.AdultsEducation.model.enums.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponse(
        Long id, String email, Role role, Boolean active,
        String firstName, String lastName, String fullName,
        String phone, String bio, String avatarUrl, LocalDate birthDate,
        // teacher-only
        String specialization, Double experienceYears,
        LocalDateTime createdAt) {}