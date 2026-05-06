package com.wairixx.AdultsEducation.service;

import com.wairixx.AdultsEducation.model.dto.course.CourseFilter;
import com.wairixx.AdultsEducation.model.dto.course.CourseRequest;
import com.wairixx.AdultsEducation.model.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {
    Course create(CourseRequest request);
    Course getById(Long id, boolean respectVisibility);
    Page<Course> getAll(CourseFilter filter, Pageable pageable, boolean onlyVisible);
    Course update(Long id, CourseRequest request);
    void delete(Long id);
    Course setVisibility(Long id, boolean visible);
}
