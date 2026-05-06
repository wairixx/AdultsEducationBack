package com.wairixx.AdultsEducation.facade.impl;

import com.wairixx.AdultsEducation.exception.BusinessException;
import com.wairixx.AdultsEducation.facade.CourseFacade;
import com.wairixx.AdultsEducation.model.dto.course.CourseFilter;
import com.wairixx.AdultsEducation.model.dto.course.CourseRequest;
import com.wairixx.AdultsEducation.model.dto.course.CourseResponse;
import com.wairixx.AdultsEducation.service.CourseService;
import com.wairixx.AdultsEducation.util.CourseMapper;
import com.wairixx.AdultsEducation.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CourseFacadeImpl implements CourseFacade {

    private final CourseService courseService;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public CourseResponse create(CourseRequest request) {
        return courseMapper.toResponse(courseService.create(request));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getById(Long id) {
        return courseMapper.toResponse(courseService.getById(id, true));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getAllPublic(CourseFilter filter, Pageable pageable) {
        return courseService.getAll(filter, pageable, true).map(courseMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getAllForAdmin(CourseFilter filter, Pageable pageable) {
        return courseService.getAll(filter, pageable, false).map(courseMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getMyAsTeacher(CourseFilter filter, Pageable pageable) {
        Long me = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException("error.auth.required"));
        CourseFilter effective = new CourseFilter(
                filter == null ? null : filter.title(),
                filter == null ? null : filter.topic(),
                filter == null ? null : filter.format(),
                filter == null ? null : filter.minPrice(),
                filter == null ? null : filter.maxPrice(),
                filter == null ? null : filter.minHours(),
                filter == null ? null : filter.maxHours(),
                me);
        return courseService.getAll(effective, pageable, false).map(courseMapper::toResponse);
    }

    @Override
    @Transactional
    public CourseResponse update(Long id, CourseRequest request) {
        return courseMapper.toResponse(courseService.update(id, request));
    }

    @Override public void delete(Long id) { courseService.delete(id); }

    @Override
    @Transactional
    public CourseResponse setVisibility(Long id, boolean visible) {
        return courseMapper.toResponse(courseService.setVisibility(id, visible));
    }
}
