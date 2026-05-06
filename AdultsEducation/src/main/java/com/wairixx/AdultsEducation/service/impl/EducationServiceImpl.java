// EducationServiceImpl.java
package com.wairixx.AdultsEducation.service.impl;

import com.wairixx.AdultsEducation.aspect.Loggable;
import com.wairixx.AdultsEducation.exception.BusinessException;
import com.wairixx.AdultsEducation.exception.ResourceNotFoundException;
import com.wairixx.AdultsEducation.model.dto.education.EducationFilter;
import com.wairixx.AdultsEducation.model.dto.education.UpdateEducationByTeacherRequest;
import com.wairixx.AdultsEducation.model.dto.education.UpdateProgressRequest;
import com.wairixx.AdultsEducation.model.entity.Education;
import com.wairixx.AdultsEducation.model.entity.Lesson;
import com.wairixx.AdultsEducation.model.entity.LessonCompletion;
import com.wairixx.AdultsEducation.model.enums.EducationLevel;
import com.wairixx.AdultsEducation.model.enums.EducationStatus;
import com.wairixx.AdultsEducation.repository.EducationRepository;
import com.wairixx.AdultsEducation.repository.LessonCompletionRepository;
import com.wairixx.AdultsEducation.repository.LessonRepository;
import com.wairixx.AdultsEducation.repository.specification.EducationSpecification;
import com.wairixx.AdultsEducation.service.CertificateService;
import com.wairixx.AdultsEducation.service.EducationService;
import com.wairixx.AdultsEducation.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Loggable
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final LessonCompletionRepository lessonCompletionRepository;
    private final LessonRepository lessonRepository;
    private final CertificateService certificateService;

    @Override
    @Transactional(readOnly = true)
    public Education getById(Long id) {
        Education e = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.education.not.found"));
        ensureCanView(e);
        return e;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Education> getAll(EducationFilter filter, Pageable pageable) {
        // тільки адмін бачить все
        if (!SecurityUtils.isAdmin())
            throw new BusinessException("error.auth.access.denied");
        return educationRepository.findAll(EducationSpecification.withFilter(filter), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Education> getMyAsStudent(EducationFilter filter, Pageable pageable) {
        Long me = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException("error.auth.required"));
        EducationFilter effective = new EducationFilter(
                me,
                filter == null ? null : filter.courseId(),
                null,
                filter == null ? null : filter.status());
        return educationRepository.findAll(EducationSpecification.withFilter(effective), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Education> getMyAsTeacher(EducationFilter filter, Pageable pageable) {
        Long me = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException("error.auth.required"));
        EducationFilter effective = new EducationFilter(
                filter == null ? null : filter.studentId(),
                filter == null ? null : filter.courseId(),
                me,
                filter == null ? null : filter.status());
        return educationRepository.findAll(EducationSpecification.withFilter(effective), pageable);
    }

    @Override
    @Transactional
    public Education updateProgress(Long id, UpdateProgressRequest request) {
        Education e = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.education.not.found"));
        ensureStudentOwner(e);
        if (e.getStatus() != EducationStatus.ACTIVE)
            throw new BusinessException("error.education.not.active");
        e.setProgress(request.progress());
        return e;
    }

    @Override
    @Transactional
    public Education completeCourse(Long id) {
        Education e = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.education.not.found"));
        ensureStudentOwner(e);
        if (e.getStatus() != EducationStatus.ACTIVE)
            throw new BusinessException("error.education.not.active");

        e.setStatus(EducationStatus.COMPLETED);
        e.setProgress(100);
        e.setIssueDate(LocalDate.now());
        int h = e.getCourse().getDurationHours();
        e.setLevel(resolveLevel(h));
        return e;
    }

    @Override
    @Transactional
    public Education updateByTeacher(Long id, UpdateEducationByTeacherRequest r) {
        Education e = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.education.not.found"));
        ensureTeacherOfCourse(e);

        if (r.status() != null) {
            if (r.status() == EducationStatus.COMPLETED && e.getStatus() != EducationStatus.COMPLETED) {
                autoComplete(e);
            } else {
                e.setStatus(r.status());
            }
        }
        if (r.level() != null) e.setLevel(r.level());
        if (r.note() != null) e.setNote(r.note());
        return e;
    }

    @Override
    @Transactional
    public Education markLessonCompleted(Long educationId, Long lessonId) {
        Education e = educationRepository.findById(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("error.education.not.found"));
        ensureStudentOwner(e);

        if (e.getStatus() != EducationStatus.ACTIVE)
            throw new BusinessException("error.education.not.active");

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("error.lesson.not.found"));

        if (!lesson.getCourse().getId().equals(e.getCourse().getId()))
            throw new BusinessException("error.lesson.not.in.course");

        // idempotent
        if (!lessonCompletionRepository.existsByEducationIdAndLessonId(educationId, lessonId)) {
            LessonCompletion lc = new LessonCompletion();
            lc.setEducation(e);
            lc.setLesson(lesson);
            lessonCompletionRepository.save(lc);
        }

        recalcProgress(e);

        // Автозавершення, якщо всі уроки зараховано
        if (e.getProgress() == 100) {
            autoComplete(e);
        }
        return e;
    }

    @Override
    @Transactional
    public Education unmarkLessonCompleted(Long educationId, Long lessonId) {
        Education e = educationRepository.findById(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("error.education.not.found"));
        ensureStudentOwner(e);
        if (e.getStatus() != EducationStatus.ACTIVE)
            throw new BusinessException("error.education.not.active");

        lessonCompletionRepository.deleteByEducationIdAndLessonId(educationId, lessonId);
        recalcProgress(e);
        return e;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getCompletedLessonIds(Long educationId) {
        Education e = educationRepository.findById(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("error.education.not.found"));
        ensureCanView(e);
        return lessonCompletionRepository.findByEducationId(educationId).stream()
                .map(lc -> lc.getLesson().getId()).toList();
    }

    private void recalcProgress(Education e) {
        long total = lessonRepository.countByCourseId(e.getCourse().getId());
        if (total == 0) { e.setProgress(0); return; }
        long done = lessonCompletionRepository.countByEducationId(e.getId());
        int percent = (int) Math.round((done * 100.0) / total);
        e.setProgress(Math.min(100, percent));
    }

    private void autoComplete(Education e) {
        e.setStatus(EducationStatus.COMPLETED);
        e.setProgress(100);
        e.setIssueDate(LocalDate.now());
        e.setLevel(resolveLevel(e.getCourse().getDurationHours()));
        certificateService.issueCertificate(e);
    }

    // ---------- helpers ----------

    private EducationLevel resolveLevel(int hours) {
        if (hours < 10) return EducationLevel.BEGINNER;
        if (hours < 40) return EducationLevel.INTERMEDIATE;
        if (hours < 100) return EducationLevel.ADVANCED;
        return EducationLevel.EXPERT;
    }

    private void ensureCanView(Education e) {
        if (SecurityUtils.isAdmin()) return;
        Long me = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException("error.auth.required"));
        if (e.getStudent().getId().equals(me)) return;
        if (e.getCourse().getTeacher().getId().equals(me)) return;
        throw new BusinessException("error.auth.access.denied");
    }

    private void ensureStudentOwner(Education e) {
        Long me = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException("error.auth.required"));
        if (!e.getStudent().getId().equals(me))
            throw new BusinessException("error.auth.access.denied");
    }

    private void ensureTeacherOfCourse(Education e) {
        if (SecurityUtils.isAdmin()) return;
        Long me = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException("error.auth.required"));
        if (!e.getCourse().getTeacher().getId().equals(me))
            throw new BusinessException("error.course.not.owner");
    }
}