package com.wairixx.AdultsEducation.service;

import com.wairixx.AdultsEducation.model.dto.lesson.LessonRequest;
import com.wairixx.AdultsEducation.model.entity.Lesson;

import java.util.List;

public interface LessonService {
    Lesson create(LessonRequest request);
    Lesson getByIdForAccess(Long id);
    List<Lesson> getLessonsForCoursePreview(Long courseId);
    List<Lesson> getLessonsForEnrolledStudent(Long courseId);
    Lesson update(Long id, LessonRequest request);
    void delete(Long id);
}