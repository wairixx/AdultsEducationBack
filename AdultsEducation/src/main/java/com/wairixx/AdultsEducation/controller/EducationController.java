package com.wairixx.AdultsEducation.controller;

import com.wairixx.AdultsEducation.facade.EducationFacade;
import com.wairixx.AdultsEducation.model.dto.education.EducationFilter;
import com.wairixx.AdultsEducation.model.dto.education.EducationResponse;
import com.wairixx.AdultsEducation.model.dto.education.UpdateEducationByTeacherRequest;
import com.wairixx.AdultsEducation.model.dto.education.UpdateProgressRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/educations")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Educations", description = "Student enrollments and teacher oversight")
public class EducationController {

    private final EducationFacade educationFacade;

    @GetMapping("/{id}")
    @Operation(summary = "Get education by ID (student, teacher or admin)")
    public ResponseEntity<EducationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(educationFacade.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all enrollments (ADMIN)")
    public ResponseEntity<Page<EducationResponse>> getAll(
            @ParameterObject EducationFilter filter,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(educationFacade.getAll(filter, pageable));
    }

    @GetMapping("/my-as-student")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get my enrollments as a student")
    public ResponseEntity<Page<EducationResponse>> getMyAsStudent(
            @ParameterObject EducationFilter filter,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(educationFacade.getMyAsStudent(filter, pageable));
    }

    @GetMapping("/my-as-teacher")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Get enrollments for my courses (TEACHER)")
    public ResponseEntity<Page<EducationResponse>> getMyAsTeacher(
            @ParameterObject EducationFilter filter,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(educationFacade.getMyAsTeacher(filter, pageable));
    }

    @PatchMapping("/{id}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Update my progress")
    public ResponseEntity<EducationResponse> updateProgress(@PathVariable Long id,
                                                            @Valid @RequestBody UpdateProgressRequest request) {
        return ResponseEntity.ok(educationFacade.updateProgress(id, request));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Complete course and generate certificate")
    public ResponseEntity<EducationResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(educationFacade.completeCourse(id));
    }

    @PatchMapping("/{id}/teacher")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @Operation(summary = "Update enrollment by teacher (status/level/note)")
    public ResponseEntity<EducationResponse> updateByTeacher(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEducationByTeacherRequest request) {
        return ResponseEntity.ok(educationFacade.updateByTeacher(id, request));
    }

    @PostMapping("/{id}/lessons/{lessonId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Mark a lesson as completed (auto-recalculates progress)")
    public ResponseEntity<EducationResponse> markLessonCompleted(
            @PathVariable Long id, @PathVariable Long lessonId) {
        return ResponseEntity.ok(educationFacade.markLessonCompleted(id, lessonId));
    }

    @DeleteMapping("/{id}/lessons/{lessonId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Unmark a lesson")
    public ResponseEntity<EducationResponse> unmarkLessonCompleted(
            @PathVariable Long id, @PathVariable Long lessonId) {
        return ResponseEntity.ok(educationFacade.unmarkLessonCompleted(id, lessonId));
    }

    @GetMapping("/{id}/completed-lessons")
    @Operation(summary = "Get list of completed lesson IDs")
    public ResponseEntity<List<Long>> getCompletedLessons(@PathVariable Long id) {
        return ResponseEntity.ok(educationFacade.getCompletedLessonIds(id));
    }
}