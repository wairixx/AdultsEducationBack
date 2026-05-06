package com.wairixx.AdultsEducation.model.dto.auth;
public record AuthResponse(String token, String email, String role, Long userId,
                           String firstName, String lastName) {}