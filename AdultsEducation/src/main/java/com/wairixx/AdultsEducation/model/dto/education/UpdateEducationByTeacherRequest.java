package com.wairixx.AdultsEducation.model.dto.education;
import com.wairixx.AdultsEducation.model.enums.EducationLevel;
import com.wairixx.AdultsEducation.model.enums.EducationStatus;
import jakarta.validation.constraints.Size;
public record UpdateEducationByTeacherRequest(
        EducationStatus status,
        EducationLevel level,
        @Size(max = 2000) String note) {}