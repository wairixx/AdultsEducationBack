package com.wairixx.AdultsEducation.service.impl;

import com.wairixx.AdultsEducation.aspect.Loggable;
import com.wairixx.AdultsEducation.exception.BusinessException;
import com.wairixx.AdultsEducation.exception.DuplicateResourceException;
import com.wairixx.AdultsEducation.exception.ResourceNotFoundException;
import com.wairixx.AdultsEducation.model.dto.lesson.LessonRequest;
import com.wairixx.AdultsEducation.model.entity.Course;
import com.wairixx.AdultsEducation.model.entity.Lesson;
import com.wairixx.AdultsEducation.model.enums.EducationStatus;
import com.wairixx.AdultsEducation.repository.CourseRepository;
import com.wairixx.AdultsEducation.repository.EducationRepository;
import com.wairixx.AdultsEducation.repository.LessonRepository;
import com.wairixx.AdultsEducation.service.LessonService;
import com.wairixx.AdultsEducation.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Loggable
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final EducationRepository educationRepository;

    @Override
    @Transactional
    public Lesson create(LessonRequest r) {
        Course course = courseRepository.findById(r.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("error.course.not.found"));
        ensureCanManageLessons(course);

        if (lessonRepository.existsByCourseIdAndOrderNumber(course.getId(), r.orderNumber()))
            throw new DuplicateResourceException("error.lesson.duplicate.order");

        Lesson l = new Lesson();
        l.setTitle(r.title());
        l.setContent(r.content());
        l.setVideoUrl(r.videoUrl());
        l.setOrderNumber(r.orderNumber());
        l.setCourse(course);
        return lessonRepository.save(l);
    }

    @Override
    @Transactional(readOnly = true)
    public Lesson getByIdForAccess(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.lesson.not.found"));
        ensureCanAccessContent(lesson.getCourse());
        return lesson;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lesson> getLessonsForCoursePreview(Long courseId) {
        if (!courseRepository.existsById(courseId))
            throw new ResourceNotFoundException("error.course.not.found");
        return lessonRepository.findByCourseIdOrderByOrderNumberAsc(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lesson> getLessonsForEnrolledStudent(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("error.course.not.found"));
        ensureCanAccessContent(course);
        return lessonRepository.findByCourseIdOrderByOrderNumberAsc(courseId);
    }

    @Override
    @Transactional
    public Lesson update(Long id, LessonRequest r) {
        Lesson existing = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.lesson.not.found"));
        ensureCanManageLessons(existing.getCourse());

        if (!existing.getCourse().getId().equals(r.courseId()))
            throw new BusinessException("error.lesson.course.change.forbidden");

        if (!existing.getOrderNumber().equals(r.orderNumber())
                && lessonRepository.existsByCourseIdAndOrderNumber(r.courseId(), r.orderNumber()))
            throw new DuplicateResourceException("error.lesson.duplicate.order");

        existing.setTitle(r.title());
        existing.setContent(r.content());
        existing.setVideoUrl(r.videoUrl());
        existing.setOrderNumber(r.orderNumber());
        return existing;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Lesson l = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.lesson.not.found"));
        ensureCanManageLessons(l.getCourse());
        lessonRepository.delete(l);
    }

    // ---------- helpers ----------

    private void ensureCanManageLessons(Course course) {
        if (SecurityUtils.isAdmin()) return;
        Long current = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException("error.auth.required"));
        if (!course.getTeacher().getId().equals(current))
            throw new BusinessException("error.course.not.owner");
    }

    /** Content accessible to: ADMIN, course owner, or enrolled student (ACTIVE/COMPLETED). */
    private void ensureCanAccessContent(Course course) {
        if (SecurityUtils.isAdmin()) return;

        Long currentId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException("error.auth.required"));

        if (course.getTeacher().getId().equals(currentId)) return;

        boolean enrolled = educationRepository.existsByStudentIdAndCourseIdAndStatusIn(
                currentId, course.getId(),
                List.of(EducationStatus.ACTIVE, EducationStatus.COMPLETED));

        if (!enrolled) throw new BusinessException("error.lesson.not.enrolled");
    }
}