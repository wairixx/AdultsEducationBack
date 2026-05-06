package com.wairixx.AdultsEducation.model.dto.auth;
import jakarta.validation.constraints.*;
public record AuthRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 100) String password) {}