package com.wairixx.AdultsEducation.model.dto.review;
import java.time.LocalDateTime;
public record ReviewResponse(
        Long id, Long courseId, String courseTitle,
        Long studentId, String studentFullName,
        Integer rating, String comment, Boolean visible,
        LocalDateTime createdAt) {}