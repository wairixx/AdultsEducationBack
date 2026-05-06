package com.wairixx.AdultsEducation.service.impl;

import com.wairixx.AdultsEducation.aspect.Loggable;
import com.wairixx.AdultsEducation.exception.BusinessException;
import com.wairixx.AdultsEducation.exception.ResourceNotFoundException;
import com.wairixx.AdultsEducation.model.dto.user.ChangePasswordRequest;
import com.wairixx.AdultsEducation.model.dto.user.UpdateMeRequest;
import com.wairixx.AdultsEducation.model.entity.StudentProfile;
import com.wairixx.AdultsEducation.model.entity.TeacherProfile;
import com.wairixx.AdultsEducation.model.entity.User;
import com.wairixx.AdultsEducation.model.enums.Role;
import com.wairixx.AdultsEducation.repository.StudentProfileRepository;
import com.wairixx.AdultsEducation.repository.TeacherProfileRepository;
import com.wairixx.AdultsEducation.repository.UserRepository;
import com.wairixx.AdultsEducation.service.MeService;
import com.wairixx.AdultsEducation.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Loggable
public class MeServiceImpl implements MeService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public User getMyProfile() { return currentOrThrow(); }

    @Override
    @Transactional
    public User updateMyProfile(UpdateMeRequest r) {
        User u = currentOrThrow();
        if (u.getRole() == Role.STUDENT) {
            StudentProfile p = studentProfileRepository.findById(u.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("error.user.not.found"));
            LocalDate targetBirthDate = r.birthDate() != null ? r.birthDate() : p.getBirthDate();
            validateAdult(targetBirthDate);
            if (r.firstName() != null)  p.setFirstName(r.firstName());
            if (r.lastName() != null)   p.setLastName(r.lastName());
            if (r.phone() != null)      p.setPhone(r.phone());
            if (r.bio() != null)        p.setBio(r.bio());
            if (r.avatarUrl() != null)  p.setAvatarUrl(r.avatarUrl());
            if (r.birthDate() != null)  p.setBirthDate(r.birthDate());
        } else if (u.getRole() == Role.TEACHER) {
            TeacherProfile p = teacherProfileRepository.findById(u.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("error.user.not.found"));
            LocalDate targetBirthDate = r.birthDate() != null ? r.birthDate() : p.getBirthDate();
            validateAdult(targetBirthDate);
            Double targetExperience = r.experienceYears() != null ? r.experienceYears() : p.getExperienceYears();
            validateTeacherExperience(targetBirthDate, targetExperience);
            if (r.firstName() != null)       p.setFirstName(r.firstName());
            if (r.lastName() != null)        p.setLastName(r.lastName());
            if (r.phone() != null)           p.setPhone(r.phone());
            if (r.bio() != null)             p.setBio(r.bio());
            if (r.avatarUrl() != null)       p.setAvatarUrl(r.avatarUrl());
            if (r.birthDate() != null)       p.setBirthDate(r.birthDate());
            if (r.specialization() != null)  p.setSpecialization(r.specialization());
            if (r.experienceYears() != null) p.setExperienceYears(r.experienceYears());
        } else {
            throw new BusinessException("error.me.admin.profile.not.editable");
        }
        return u;
    }

    @Override
    @Transactional
    public void changeMyPassword(ChangePasswordRequest r) {
        User u = currentOrThrow();
        if (!passwordEncoder.matches(r.currentPassword(), u.getPassword()))
            throw new BusinessException("error.me.password.current.wrong");
        if (passwordEncoder.matches(r.newPassword(), u.getPassword()))
            throw new BusinessException("error.me.password.same");
        u.setPassword(passwordEncoder.encode(r.newPassword()));
    }

    private User currentOrThrow() {
        Long id = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException("error.auth.required"));
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.user.not.found"));
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

    private void validateTeacherExperience(LocalDate birthDate, Double experienceYears) {
        if (experienceYears == null) return;
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (experienceYears > age) {
            throw new BusinessException("error.teacher.experience.exceeds.age");
        }
    }
}