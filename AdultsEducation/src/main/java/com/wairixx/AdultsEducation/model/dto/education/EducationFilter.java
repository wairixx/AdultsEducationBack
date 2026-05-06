package com.wairixx.AdultsEducation.model.dto.education;
import com.wairixx.AdultsEducation.model.enums.EducationStatus;
public record EducationFilter(Long studentId, Long courseId, Long teacherId, EducationStatus status) {}