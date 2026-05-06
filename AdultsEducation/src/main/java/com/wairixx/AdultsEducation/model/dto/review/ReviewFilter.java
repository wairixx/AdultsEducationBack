package com.wairixx.AdultsEducation.model.dto.review;
public record ReviewFilter(Long courseId, Long studentId, Integer minRating, Integer maxRating) {}