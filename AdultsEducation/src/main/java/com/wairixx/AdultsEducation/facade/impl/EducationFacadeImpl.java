package com.wairixx.AdultsEducation.facade.impl;

import com.wairixx.AdultsEducation.facade.EducationFacade;
import com.wairixx.AdultsEducation.model.dto.education.EducationFilter;
import com.wairixx.AdultsEducation.model.dto.education.EducationResponse;
import com.wairixx.AdultsEducation.model.dto.education.UpdateEducationByTeacherRequest;
import com.wairixx.AdultsEducation.model.dto.education.UpdateProgressRequest;
import com.wairixx.AdultsEducation.service.EducationService;
import com.wairixx.AdultsEducation.util.EducationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EducationFacadeImpl implements EducationFacade {

    private final EducationService educationService;
    private final EducationMapper educationMapper;

    @Override
    @Transactional(readOnly = true)
    public EducationResponse getById(Long id) {
        return educationMapper.toResponse(educationService.getById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EducationResponse> getAll(EducationFilter filter, Pageable pageable) {
        return educationService.getAll(filter, pageable).map(educationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EducationResponse> getMyAsStudent(EducationFilter filter, Pageable pageable) {
        return educationService.getMyAsStudent(filter, pageable).map(educationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EducationResponse> getMyAsTeacher(EducationFilter filter, Pageable pageable) {
        return educationService.getMyAsTeacher(filter, pageable).map(educationMapper::toResponse);
    }

    @Override
    @Transactional
    public EducationResponse updateProgress(Long id, UpdateProgressRequest request) {
        return educationMapper.toResponse(educationService.updateProgress(id, request));
    }

    @Override
    @Transactional
    public EducationResponse completeCourse(Long id) {
        return educationMapper.toResponse(educationService.completeCourse(id));
    }

    @Override
    @Transactional
    public EducationResponse updateByTeacher(Long id, UpdateEducationByTeacherRequest request) {
        return educationMapper.toResponse(educationService.updateByTeacher(id, request));
    }

    @Override
    @Transactional
    public EducationResponse markLessonCompleted(Long id, Long lessonId) {
        return educationMapper.toResponse(educationService.markLessonCompleted(id, lessonId));
    }

    @Override
    @Transactional
    public EducationResponse unmarkLessonCompleted(Long id, Long lessonId) {
        return educationMapper.toResponse(educationService.unmarkLessonCompleted(id, lessonId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getCompletedLessonIds(Long id) {
        return educationService.getCompletedLessonIds(id);
    }
}