package com.wairixx.AdultsEducation.controller;

import com.wairixx.AdultsEducation.facade.ReviewFacade;
import com.wairixx.AdultsEducation.model.dto.review.ReviewFilter;
import com.wairixx.AdultsEducation.model.dto.review.ReviewRequest;
import com.wairixx.AdultsEducation.model.dto.review.ReviewResponse;
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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Course reviews")
public class ReviewController {

    private final ReviewFacade reviewFacade;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Leave a review (STUDENT, must be enrolled)")
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest request) {
        return new ResponseEntity<>(reviewFacade.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID (public)")
    public ResponseEntity<ReviewResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewFacade.getById(id));
    }

    @GetMapping
    @Operation(summary = "List visible reviews (public)")
    public ResponseEntity<Page<ReviewResponse>> getAll(
            @ParameterObject ReviewFilter filter,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(reviewFacade.getAllPublic(filter, pageable));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all reviews including hidden (ADMIN)")
    public ResponseEntity<Page<ReviewResponse>> getAllForAdmin(
            @ParameterObject ReviewFilter filter,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(reviewFacade.getAllForAdmin(filter, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    @Operation(summary = "Delete review (author or admin)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewFacade.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/visibility")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Show/hide review (ADMIN moderation)")
    public ResponseEntity<ReviewResponse> setVisibility(@PathVariable Long id,
                                                        @RequestParam boolean visible) {
        return ResponseEntity.ok(reviewFacade.setVisibility(id, visible));
    }
}