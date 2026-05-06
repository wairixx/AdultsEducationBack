package com.wairixx.AdultsEducation.facade;

import com.wairixx.AdultsEducation.model.dto.course.CourseFilter;
import com.wairixx.AdultsEducation.model.dto.course.CourseRequest;
import com.wairixx.AdultsEducation.model.dto.course.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseFacade {
    CourseResponse create(CourseRequest request);
    CourseResponse getById(Long id);
    Page<CourseResponse> getAllPublic(CourseFilter filter, Pageable pageable);
    Page<CourseResponse> getAllForAdmin(CourseFilter filter, Pageable pageable);
    Page<CourseResponse> getMyAsTeacher(CourseFilter filter, Pageable pageable);
    CourseResponse update(Long id, CourseRequest request);
    void delete(Long id);
    CourseResponse setVisibility(Long id, boolean visible);
}