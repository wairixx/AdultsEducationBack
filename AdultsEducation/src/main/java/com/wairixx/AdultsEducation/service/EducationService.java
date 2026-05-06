package com.wairixx.AdultsEducation.service;

import com.wairixx.AdultsEducation.model.dto.education.EducationFilter;
import com.wairixx.AdultsEducation.model.dto.education.UpdateEducationByTeacherRequest;
import com.wairixx.AdultsEducation.model.dto.education.UpdateProgressRequest;
import com.wairixx.AdultsEducation.model.entity.Education;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EducationService {
    Education getById(Long id);
    Page<Education> getAll(EducationFilter filter, Pageable pageable);
    Page<Education> getMyAsStudent(EducationFilter filter, Pageable pageable);
    Page<Education> getMyAsTeacher(EducationFilter filter, Pageable pageable);
    Education updateProgress(Long id, UpdateProgressRequest request);
    Education completeCourse(Long id);
    Education updateByTeacher(Long id, UpdateEducationByTeacherRequest request);
    Education markLessonCompleted(Long educationId, Long lessonId);
    Education unmarkLessonCompleted(Long educationId, Long lessonId);
    List<Long> getCompletedLessonIds(Long educationId);
}