package com.wairixx.AdultsEducation.model.dto.education;

import com.wairixx.AdultsEducation.model.enums.EducationLevel;
import com.wairixx.AdultsEducation.model.enums.EducationStatus;
import java.time.LocalDate;

public record EducationResponse(
        Long id,
        Long studentId, String studentFullName, String studentAvatarUrl,
        Long courseId, String courseTitle, String courseCoverUrl,
        EducationStatus status,
        LocalDate enrolledDate, LocalDate issueDate,
        EducationLevel level, Integer progress, String note) {}