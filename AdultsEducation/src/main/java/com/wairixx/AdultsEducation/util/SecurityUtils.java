package com.wairixx.AdultsEducation.util;

import com.wairixx.AdultsEducation.config.security.UserDetailsImpl;
import com.wairixx.AdultsEducation.model.entity.User;
import com.wairixx.AdultsEducation.model.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        if (!(auth.getPrincipal() instanceof UserDetailsImpl d)) return Optional.empty();
        return Optional.of(d.getUser());
    }

    public static Optional<Long> getCurrentUserId() {
        return getCurrentUser().map(User::getId);
    }

    public static boolean isAdmin() {
        return getCurrentUser().map(u -> u.getRole() == Role.ADMIN).orElse(false);
    }

    public static boolean isTeacher() {
        return getCurrentUser().map(u -> u.getRole() == Role.TEACHER).orElse(false);
    }

    public static boolean isStudent() {
        return getCurrentUser().map(u -> u.getRole() == Role.STUDENT).orElse(false);
    }
}