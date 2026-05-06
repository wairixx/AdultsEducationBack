package com.wairixx.AdultsEducation.controller;

import com.wairixx.AdultsEducation.facade.EnrollmentFacade;
import com.wairixx.AdultsEducation.model.dto.education.EnrollmentRequest;
import com.wairixx.AdultsEducation.model.dto.education.EnrollmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@Tag(name = "Enrollments", description = "Course enrollment (Unit of Work: Education + Payment)")
public class EnrollmentController {

    private final EnrollmentFacade enrollmentFacade;

    @PostMapping
    @Operation(summary = "Enroll into a course (atomic: creates Education + Payment)")
    public ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody EnrollmentRequest request) {
        return new ResponseEntity<>(enrollmentFacade.enroll(request), HttpStatus.CREATED);
    }
}