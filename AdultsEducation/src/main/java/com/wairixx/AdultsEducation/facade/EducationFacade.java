package com.wairixx.AdultsEducation.facade;

import com.wairixx.AdultsEducation.model.dto.education.EducationFilter;
import com.wairixx.AdultsEducation.model.dto.education.EducationResponse;
import com.wairixx.AdultsEducation.model.dto.education.UpdateEducationByTeacherRequest;
import com.wairixx.AdultsEducation.model.dto.education.UpdateProgressRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EducationFacade {
    EducationResponse getById(Long id);
    Page<EducationResponse> getAll(EducationFilter filter, Pageable pageable);
    Page<EducationResponse> getMyAsStudent(EducationFilter filter, Pageable pageable);
    Page<EducationResponse> getMyAsTeacher(EducationFilter filter, Pageable pageable);
    EducationResponse updateProgress(Long id, UpdateProgressRequest request);
    EducationResponse completeCourse(Long id);
    EducationResponse updateByTeacher(Long id, UpdateEducationByTeacherRequest request);
    EducationResponse markLessonCompleted(Long id, Long lessonId);
    EducationResponse unmarkLessonCompleted(Long id, Long lessonId);
    List<Long> getCompletedLessonIds(Long id);
}