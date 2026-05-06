package com.wairixx.AdultsEducation.model.dto.review;
import jakarta.validation.constraints.*;
public record ReviewRequest(
        @NotNull(message = "{error.review.course.required}") Long courseId,
        @NotNull(message = "{error.review.rating.required}")
        @Min(value = 1, message = "{error.review.rating.range}")
        @Max(value = 5, message = "{error.review.rating.range}") Integer rating,
        @Size(max = 2000) String comment,
        Long studentId) {}