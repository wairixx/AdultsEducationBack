package com.wairixx.AdultsEducation.model.dto.user;
import jakarta.validation.constraints.*;
public record ChangePasswordRequest(
        @NotBlank(message = "{error.me.password.current.blank}") String currentPassword,
        @NotBlank(message = "{error.me.password.new.blank}")
        @Size(min = 6, max = 100, message = "{error.me.password.new.size}") String newPassword) {}