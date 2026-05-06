package com.wairixx.AdultsEducation.service.impl;

import com.wairixx.AdultsEducation.aspect.Loggable;
import com.wairixx.AdultsEducation.exception.DuplicateResourceException;
import com.wairixx.AdultsEducation.exception.ResourceNotFoundException;
import com.wairixx.AdultsEducation.model.dto.user.UpdateUserRequest;
import com.wairixx.AdultsEducation.model.dto.user.UserFilter;
import com.wairixx.AdultsEducation.model.entity.User;
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

        if (r.email() != null && !r.email().equals(u.getEmail())) {
            if (userRepository.existsByEmail(r.email()))
                throw new DuplicateResourceException("error.user.email.taken");
            u.setEmail(r.email());
        }
        if (r.role() != null) u.setRole(r.role());
        if (r.active() != null) u.setActive(r.active());

        // оновлюємо поля у відповідному профілі
        studentProfileRepository.findById(id).ifPresent(p -> {
            if (r.firstName() != null) p.setFirstName(r.firstName());
            if (r.lastName() != null)  p.setLastName(r.lastName());
            if (r.phone() != null)     p.setPhone(r.phone());
            if (r.bio() != null)       p.setBio(r.bio());
            if (r.avatarUrl() != null) p.setAvatarUrl(r.avatarUrl());
        });
        teacherProfileRepository.findById(id).ifPresent(p -> {
            if (r.firstName() != null)       p.setFirstName(r.firstName());
            if (r.lastName() != null)        p.setLastName(r.lastName());
            if (r.phone() != null)           p.setPhone(r.phone());
            if (r.bio() != null)             p.setBio(r.bio());
            if (r.avatarUrl() != null)       p.setAvatarUrl(r.avatarUrl());
            if (r.specialization() != null)  p.setSpecialization(r.specialization());
            if (r.experienceYears() != null) p.setExperienceYears(r.experienceYears());
        });
        return u;
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
}