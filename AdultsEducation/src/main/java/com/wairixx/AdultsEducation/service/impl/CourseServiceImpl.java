package com.wairixx.AdultsEducation.service.impl;

import com.wairixx.AdultsEducation.aspect.Loggable;
import com.wairixx.AdultsEducation.exception.BusinessException;
import com.wairixx.AdultsEducation.exception.DuplicateResourceException;
import com.wairixx.AdultsEducation.exception.ResourceNotFoundException;
import com.wairixx.AdultsEducation.model.dto.course.CourseFilter;
import com.wairixx.AdultsEducation.model.dto.course.CourseRequest;
import com.wairixx.AdultsEducation.model.entity.Course;
import com.wairixx.AdultsEducation.model.entity.TeacherProfile;
import com.wairixx.AdultsEducation.model.entity.User;
import com.wairixx.AdultsEducation.model.enums.Role;
import com.wairixx.AdultsEducation.repository.CourseRepository;
import com.wairixx.AdultsEducation.repository.TeacherProfileRepository;
import com.wairixx.AdultsEducation.repository.specification.CourseSpecification;
import com.wairixx.AdultsEducation.service.CourseService;
import com.wairixx.AdultsEducation.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Loggable
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final TeacherProfileRepository teacherProfileRepository;

    @Override
    @Transactional
    public Course create(CourseRequest r) {
        User current = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new BusinessException("error.auth.required"));

        TeacherProfile teacher;
        if (current.getRole() == Role.ADMIN && r.teacherId() != null) {
            // Admin creates course on behalf of a specific teacher
            teacher = teacherProfileRepository.findById(r.teacherId())
                    .orElseThrow(() -> new BusinessException("error.course.teacher.profile.missing"));
        } else if (current.getRole() == Role.TEACHER || current.getRole() == Role.ADMIN) {
            teacher = teacherProfileRepository.findById(current.getId())
                    .orElseThrow(() -> new BusinessException("error.course.teacher.profile.missing"));
        } else {
            throw new BusinessException("error.course.only.teacher");
        }

        if (courseRepository.existsByTitleAndTeacherId(r.title(), teacher.getId()))
            throw new DuplicateResourceException("error.course.duplicate.title");

        Course c = new Course();
        c.setTitle(r.title());
        c.setDescription(r.description());
        c.setTopic(r.topic());
        c.setFormat(r.format());
        c.setPrice(r.price());
        c.setDurationHours(r.durationHours());
        c.setTeacher(teacher);
        c.setCoverUrl(r.coverUrl());
        c.setVisible(true);
        return courseRepository.save(c);
    }

    @Override
    @Transactional(readOnly = true)
    public Course getById(Long id, boolean respectVisibility) {
        Course c = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.course.not.found"));
        if (respectVisibility && !c.getVisible() && !SecurityUtils.isAdmin()
                && !isOwnerOrNone(c)) {
            throw new ResourceNotFoundException("error.course.not.found");
        }
        return c;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Course> getAll(CourseFilter filter, Pageable pageable, boolean onlyVisible) {
        return courseRepository.findAll(CourseSpecification.withFilter(filter, onlyVisible), pageable);
    }

    @Override
    @Transactional
    public Course update(Long id, CourseRequest r) {
        Course c = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.course.not.found"));
        ensureCanModify(c);

        boolean titleChanged = !c.getTitle().equals(r.title());
        if (titleChanged && courseRepository.existsByTitleAndTeacherId(r.title(), c.getTeacher().getId()))
            throw new DuplicateResourceException("error.course.duplicate.title");

        c.setTitle(r.title());
        c.setDescription(r.description());
        c.setTopic(r.topic());
        c.setFormat(r.format());
        c.setPrice(r.price());
        c.setDurationHours(r.durationHours());
        c.setCoverUrl(r.coverUrl());
        return c;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Course c = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.course.not.found"));
        ensureCanModify(c);
        courseRepository.delete(c);
    }

    @Override
    @Transactional
    public Course setVisibility(Long id, boolean visible) {
        Course c = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.course.not.found"));
        // Admin — будь-який курс; викладач — тільки свій
        if (!SecurityUtils.isAdmin()) ensureOwner(c);
        c.setVisible(visible);
        return c;
    }

    private void ensureCanModify(Course c) {
        if (SecurityUtils.isAdmin()) return;
        ensureOwner(c);
    }

    private void ensureOwner(Course c) {
        Long currentId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException("error.auth.required"));
        if (!c.getTeacher().getId().equals(currentId))
            throw new BusinessException("error.course.not.owner");
    }

    private boolean isOwnerOrNone(Course c) {
        return SecurityUtils.getCurrentUserId()
                .map(id -> c.getTeacher().getId().equals(id)).orElse(false);
    }
}