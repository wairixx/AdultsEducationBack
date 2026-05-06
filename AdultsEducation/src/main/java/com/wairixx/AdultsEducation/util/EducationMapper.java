package com.wairixx.AdultsEducation.util;

import com.wairixx.AdultsEducation.model.dto.education.EducationResponse;
import com.wairixx.AdultsEducation.model.entity.Education;
import org.springframework.stereotype.Component;

@Component
public class EducationMapper {

    public EducationResponse toResponse(Education e) {
        return new EducationResponse(
                e.getId(),
                e.getStudent().getId(),
                NameUtils.fullName(e.getStudent().getLastName(), e.getStudent().getFirstName()),
                e.getStudent().getAvatarUrl(),
                e.getCourse().getId(), e.getCourse().getTitle(), e.getCourse().getCoverUrl(),
                e.getStatus(), e.getEnrolledDate(), e.getIssueDate(),
                e.getLevel(), e.getProgress(), e.getNote());
    }
}