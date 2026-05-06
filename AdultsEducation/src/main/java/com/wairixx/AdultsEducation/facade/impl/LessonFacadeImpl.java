package com.wairixx.AdultsEducation.facade.impl;

import com.wairixx.AdultsEducation.facade.LessonFacade;
import com.wairixx.AdultsEducation.model.dto.lesson.LessonPreviewResponse;
import com.wairixx.AdultsEducation.model.dto.lesson.LessonRequest;
import com.wairixx.AdultsEducation.model.dto.lesson.LessonResponse;
import com.wairixx.AdultsEducation.service.LessonService;
import com.wairixx.AdultsEducation.util.LessonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LessonFacadeImpl implements LessonFacade {

    private final LessonService lessonService;
    private final LessonMapper lessonMapper;

    @Override
    @Transactional
    public LessonResponse create(LessonRequest request) {
        return lessonMapper.toResponse(lessonService.create(request));
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getById(Long id) {
        return lessonMapper.toResponse(lessonService.getByIdForAccess(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonPreviewResponse> getCoursePreview(Long courseId) {
        return lessonService.getLessonsForCoursePreview(courseId)
                .stream().map(lessonMapper::toPreview).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> getCourseFull(Long courseId) {
        return lessonService.getLessonsForEnrolledStudent(courseId)
                .stream().map(lessonMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public LessonResponse update(Long id, LessonRequest request) {
        return lessonMapper.toResponse(lessonService.update(id, request));
    }

    @Override public void delete(Long id) { lessonService.delete(id); }
}