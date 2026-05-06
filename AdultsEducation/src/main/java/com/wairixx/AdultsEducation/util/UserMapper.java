package com.wairixx.AdultsEducation.util;

import com.wairixx.AdultsEducation.model.dto.user.UserResponse;
import com.wairixx.AdultsEducation.model.entity.StudentProfile;
import com.wairixx.AdultsEducation.model.entity.TeacherProfile;
import com.wairixx.AdultsEducation.model.entity.User;
import com.wairixx.AdultsEducation.model.enums.Role;
import com.wairixx.AdultsEducation.repository.StudentProfileRepository;
import com.wairixx.AdultsEducation.repository.TeacherProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final StudentProfileRepository studentProfileRepository;
    private final TeacherProfileRepository teacherProfileRepository;

    public UserResponse toResponse(User u) {
        if (u.getRole() == Role.TEACHER) {
            TeacherProfile t = teacherProfileRepository.findById(u.getId()).orElse(null);
            return new UserResponse(
                    u.getId(), u.getEmail(), u.getRole(), u.getActive(),
                    t == null ? null : t.getFirstName(),
                    t == null ? null : t.getLastName(),
                    t == null ? null : NameUtils.fullName(t.getLastName(), t.getFirstName()),
                    t == null ? null : t.getPhone(),
                    t == null ? null : t.getBio(),
                    t == null ? null : t.getAvatarUrl(),
                    t == null ? null : t.getBirthDate(),
                    t == null ? null : t.getSpecialization(),
                    t == null ? null : t.getExperienceYears(),
                    u.getCreatedAt());
        }

        if (u.getRole() == Role.STUDENT) {
            StudentProfile s = studentProfileRepository.findById(u.getId()).orElse(null);
            return new UserResponse(
                    u.getId(), u.getEmail(), u.getRole(), u.getActive(),
                    s == null ? null : s.getFirstName(),
                    s == null ? null : s.getLastName(),
                    s == null ? null : NameUtils.fullName(s.getLastName(), s.getFirstName()),
                    s == null ? null : s.getPhone(),
                    s == null ? null : s.getBio(),
                    s == null ? null : s.getAvatarUrl(),
                    s == null ? null : s.getBirthDate(),
                    null, null,
                    u.getCreatedAt());
        }

        return new UserResponse(u.getId(), u.getEmail(), u.getRole(), u.getActive(),
                null, null, null, null, null, null, null, null, null, u.getCreatedAt());
    }
}