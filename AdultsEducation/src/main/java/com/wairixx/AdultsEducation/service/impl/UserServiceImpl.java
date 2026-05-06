package com.wairixx.AdultsEducation.service.impl;

import com.wairixx.AdultsEducation.aspect.Loggable;
import com.wairixx.AdultsEducation.exception.BusinessException;
import com.wairixx.AdultsEducation.exception.DuplicateResourceException;
import com.wairixx.AdultsEducation.exception.ResourceNotFoundException;
import com.wairixx.AdultsEducation.model.dto.user.UpdateUserRequest;
import com.wairixx.AdultsEducation.model.dto.user.UserFilter;
import com.wairixx.AdultsEducation.model.entity.AbstractProfile;
import com.wairixx.AdultsEducation.model.entity.StudentProfile;
import com.wairixx.AdultsEducation.model.entity.TeacherProfile;
import com.wairixx.AdultsEducation.model.entity.User;
import com.wairixx.AdultsEducation.model.enums.Role;
import com.wairixx.AdultsEducation.repository.StudentProfileRepository;
import com.wairixx.AdultsEducation.repository.TeacherProfileRepository;
import com.wairixx.AdultsEducation.repository.UserRepository;
import com.wairixx.AdultsEducation.repository.specification.UserSpecification;
import com.wairixx.AdultsEducation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Loggable
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final TeacherProfileRepository teacherProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.user.not.found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAll(UserFilter filter, Pageable pageable) {
        return userRepository.findAll(UserSpecification.withFilter(filter), pageable);
    }

    @Override
    @Transactional
    public User update(Long id, UpdateUserRequest r) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.user.not.found"));
        Role originalRole = u.getRole();

        StudentProfile existingStudentProfile = studentProfileRepository.findById(id).orElse(null);
        TeacherProfile existingTeacherProfile = teacherProfileRepository.findById(id).orElse(null);
        Role targetRole = r.role() != null ? r.role() : u.getRole();
        boolean roleChanged = targetRole != originalRole;

        if (r.email() != null && !r.email().equals(u.getEmail())) {
            if (userRepository.existsByEmail(r.email()))
                throw new DuplicateResourceException("error.user.email.taken");
            u.setEmail(r.email());
        }
        if (r.role() != null) u.setRole(r.role());
        if (r.active() != null) u.setActive(r.active());

        if (targetRole == Role.STUDENT) {
            AbstractProfile sourceProfile = roleChanged && originalRole == Role.TEACHER
                    ? existingTeacherProfile
                    : existingStudentProfile;
            upsertStudentProfile(u, r, existingStudentProfile, sourceProfile);
            if (existingTeacherProfile != null) {
                teacherProfileRepository.delete(existingTeacherProfile);
            }
        } else if (targetRole == Role.TEACHER) {
            AbstractProfile sourceProfile = roleChanged && originalRole == Role.STUDENT
                    ? existingStudentProfile
                    : existingTeacherProfile;
            upsertTeacherProfile(u, r, existingTeacherProfile, sourceProfile);
            if (existingStudentProfile != null) {
                studentProfileRepository.delete(existingStudentProfile);
            }
        } else {
            if (existingStudentProfile != null) studentProfileRepository.delete(existingStudentProfile);
            if (existingTeacherProfile != null) teacherProfileRepository.delete(existingTeacherProfile);
        }
        return u;
    }

    private void upsertStudentProfile(
            User user,
            UpdateUserRequest request,
            StudentProfile existingStudentProfile,
            AbstractProfile sourceProfile
    ) {
        StudentProfile profile = existingStudentProfile != null ? existingStudentProfile : new StudentProfile();
        profile.setUser(user);
        applyCommonFields(profile, request, sourceProfile);
        studentProfileRepository.save(profile);
    }

    private void upsertTeacherProfile(
            User user,
            UpdateUserRequest request,
            TeacherProfile existingTeacherProfile,
            AbstractProfile sourceProfile
    ) {
        TeacherProfile profile = existingTeacherProfile != null ? existingTeacherProfile : new TeacherProfile();
        profile.setUser(user);
        applyCommonFields(profile, request, sourceProfile);

        if (request.specialization() != null) {
            profile.setSpecialization(request.specialization());
        }
        Double targetExperience = request.experienceYears() != null ? request.experienceYears() : profile.getExperienceYears();
        validateTeacherExperience(profile.getBirthDate(), targetExperience);
        if (request.experienceYears() != null) {
            profile.setExperienceYears(request.experienceYears());
        }
        teacherProfileRepository.save(profile);
    }

    private void applyCommonFields(AbstractProfile target, UpdateUserRequest request, AbstractProfile source) {
        String firstName = request.firstName() != null
                ? request.firstName()
                : (source != null ? source.getFirstName() : target.getFirstName());
        String lastName = request.lastName() != null
                ? request.lastName()
                : (source != null ? source.getLastName() : target.getLastName());

        if (firstName == null || firstName.isBlank()) {
            throw new BusinessException("error.user.firstname.blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new BusinessException("error.user.lastname.blank");
        }

        target.setFirstName(firstName);
        target.setLastName(lastName);

        if (request.phone() != null) {
            target.setPhone(request.phone());
        } else if (source != null && target.getPhone() == null) {
            target.setPhone(source.getPhone());
        }

        if (request.bio() != null) {
            target.setBio(request.bio());
        } else if (source != null && target.getBio() == null) {
            target.setBio(source.getBio());
        }

        if (request.avatarUrl() != null) {
            target.setAvatarUrl(request.avatarUrl());
        } else if (source != null && target.getAvatarUrl() == null) {
            target.setAvatarUrl(source.getAvatarUrl());
        }

        LocalDate birthDate = request.birthDate() != null
                ? request.birthDate()
                : (source != null ? source.getBirthDate() : target.getBirthDate());
        validateAdult(birthDate);
        target.setBirthDate(birthDate);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id))
            throw new ResourceNotFoundException("error.user.not.found");

        // Profiles share PK with user and hold FK to users.id.
        // Delete profiles first to avoid DB integrity conflicts.
        if (studentProfileRepository.existsById(id)) {
            studentProfileRepository.deleteById(id);
        }
        if (teacherProfileRepository.existsById(id)) {
            teacherProfileRepository.deleteById(id);
        }

        userRepository.deleteById(id);
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