package com.wairixx.AdultsEducation.model.dto.auth;

import com.wairixx.AdultsEducation.model.enums.Role;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "{error.user.email.blank}")
        @Email(message = "{error.user.email.invalid}")
        String email,
        @NotBlank(message = "{error.user.password.blank}")
        @Size(min = 6, max = 100, message = "{error.user.password.size}")
        String password,
        @NotBlank(message = "{error.user.firstname.blank}")
        @Size(max = 50) String firstName,
        @NotBlank(message = "{error.user.lastname.blank}")
        @Size(max = 50) String lastName,
        @Size(max = 20) String phone,
        @Size(max = 500) String avatarUrl,
        Role role) {}