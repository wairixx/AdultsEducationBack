package com.wairixx.AdultsEducation.controller;

import com.wairixx.AdultsEducation.facade.CourseFacade;
import com.wairixx.AdultsEducation.model.dto.course.CourseFilter;
import com.wairixx.AdultsEducation.model.dto.course.CourseRequest;
import com.wairixx.AdultsEducation.model.dto.course.CourseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course catalog and management")
public class CourseController {

    private final CourseFacade courseFacade;

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @Operation(summary = "Create a new course (TEACHER/ADMIN)")
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request) {
        return new ResponseEntity<>(courseFacade.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID (public)")
    public ResponseEntity<CourseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseFacade.getById(id));
    }

    @GetMapping
    @Operation(summary = "List visible courses (public)")
    public ResponseEntity<Page<CourseResponse>> getAll(
            @ParameterObject CourseFilter filter,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(courseFacade.getAllPublic(filter, pageable));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all courses including hidden (ADMIN)")
    public ResponseEntity<Page<CourseResponse>> getAllForAdmin(
            @ParameterObject CourseFilter filter,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(courseFacade.getAllForAdmin(filter, pageable));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Get my courses (TEACHER)")
    public ResponseEntity<Page<CourseResponse>> getMy(
            @ParameterObject CourseFilter filter,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(courseFacade.getMyAsTeacher(filter, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @Operation(summary = "Update course (owner or admin)")
    public ResponseEntity<CourseResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseFacade.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @Operation(summary = "Delete course (owner or admin)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseFacade.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/visibility")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @Operation(summary = "Show/hide course")
    public ResponseEntity<CourseResponse> setVisibility(@PathVariable Long id,
                                                        @RequestParam boolean visible) {
        return ResponseEntity.ok(courseFacade.setVisibility(id, visible));
    }
}