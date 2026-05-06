package com.wairixx.AdultsEducation.controller;

import com.wairixx.AdultsEducation.facade.LessonFacade;
import com.wairixx.AdultsEducation.model.dto.lesson.LessonPreviewResponse;
import com.wairixx.AdultsEducation.model.dto.lesson.LessonRequest;
import com.wairixx.AdultsEducation.model.dto.lesson.LessonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons", description = "Course lessons")
public class LessonController {

    private final LessonFacade lessonFacade;

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @Operation(summary = "Create a lesson (owner or admin)")
    public ResponseEntity<LessonResponse> create(@Valid @RequestBody LessonRequest request) {
        return new ResponseEntity<>(lessonFacade.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get lesson with full content (requires enrollment)")
    public ResponseEntity<LessonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonFacade.getById(id));
    }

    @GetMapping("/by-course/{courseId}/preview")
    @Operation(summary = "Get only titles+order of lessons (public preview)")
    public ResponseEntity<List<LessonPreviewResponse>> getPreview(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonFacade.getCoursePreview(courseId));
    }

    @GetMapping("/by-course/{courseId}/full")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get full lessons (enrolled students, teacher, admin)")
    public ResponseEntity<List<LessonResponse>> getFull(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonFacade.getCourseFull(courseId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @Operation(summary = "Update lesson (owner or admin)")
    public ResponseEntity<LessonResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(lessonFacade.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @Operation(summary = "Delete lesson")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lessonFacade.delete(id);
        return ResponseEntity.noContent().build();
    }
}