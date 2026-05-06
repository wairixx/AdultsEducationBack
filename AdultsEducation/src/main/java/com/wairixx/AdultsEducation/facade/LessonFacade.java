package com.wairixx.AdultsEducation.facade;

import com.wairixx.AdultsEducation.model.dto.lesson.LessonPreviewResponse;
import com.wairixx.AdultsEducation.model.dto.lesson.LessonRequest;
import com.wairixx.AdultsEducation.model.dto.lesson.LessonResponse;

import java.util.List;

public interface LessonFacade {
    LessonResponse create(LessonRequest request);
    LessonResponse getById(Long id);
    List<LessonPreviewResponse> getCoursePreview(Long courseId);
    List<LessonResponse> getCourseFull(Long courseId);
    LessonResponse update(Long id, LessonRequest request);
    void delete(Long id);
}