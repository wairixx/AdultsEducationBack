package com.wairixx.AdultsEducation.service.security;

import com.wairixx.AdultsEducation.aspect.Loggable;
import com.wairixx.AdultsEducation.config.security.UserDetailsImpl;
import com.wairixx.AdultsEducation.exception.BusinessException;
import com.wairixx.AdultsEducation.exception.DuplicateResourceException;
import com.wairixx.AdultsEducation.model.dto.auth.AuthRequest;
import com.wairixx.AdultsEducation.model.dto.auth.AuthResponse;
import com.wairixx.AdultsEducation.model.dto.auth.RegisterRequest;
import com.wairixx.AdultsEducation.model.entity.StudentProfile;
import com.wairixx.AdultsEducation.model.entity.TeacherProfile;
import com.wairixx.AdultsEducation.model.entity.User;
import com.wairixx.AdultsEducation.model.enums.Role;
import com.wairixx.AdultsEducation.repository.StudentProfileRepository;
import com.wairixx.AdultsEducation.repository.TeacherProfileRepository;
import com.wairixx.AdultsEducation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Loggable(logArgs = false)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();
        User user = details.getUser();
        return buildResponse(user);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email()))
            throw new DuplicateResourceException("error.user.duplicate.email");
        validateAdult(request.birthDate());

        Role role = request.role() == null ? Role.STUDENT : request.role();
        if (role == Role.ADMIN)
            throw new BusinessException("error.user.register.admin.forbidden");

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);
        user.setActive(true);
        user = userRepository.save(user);

        if (role == Role.STUDENT) {
            StudentProfile p = new StudentProfile();
            p.setUser(user);
            p.setFirstName(request.firstName());
            p.setLastName(request.lastName());
            p.setPhone(request.phone());
            p.setAvatarUrl(request.avatarUrl());
            p.setBirthDate(request.birthDate());
            studentProfileRepository.save(p);
        } else {
            TeacherProfile p = new TeacherProfile();
            p.setUser(user);
            p.setFirstName(request.firstName());
            p.setLastName(request.lastName());
            p.setPhone(request.phone());
            p.setAvatarUrl(request.avatarUrl());
            p.setBirthDate(request.birthDate());
            teacherProfileRepository.save(p);
        }

        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        String first = null, last = null;
        if (user.getRole() == Role.STUDENT) {
            var p = studentProfileRepository.findById(user.getId()).orElse(null);
            if (p != null) { first = p.getFirstName(); last = p.getLastName(); }
        } else if (user.getRole() == Role.TEACHER) {
            var p = teacherProfileRepository.findById(user.getId()).orElse(null);
            if (p != null) { first = p.getFirstName(); last = p.getLastName(); }
        }
        return new AuthResponse(
                jwtService.generateToken(user),
                user.getEmail(), user.getRole().name(), user.getId(),
                first, last);
    }

    private void validateAdult(LocalDate birthDate) {
        if (birthDate == null) {
            throw new BusinessException("error.user.birthdate.required");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new BusinessException("error.user.birthdate.future");
        }
        if (Period.between(birthDate, LocalDate.now()).getYears() < 18) {
            throw new BusinessException("error.user.birthdate.age.min");
        }
    }
}