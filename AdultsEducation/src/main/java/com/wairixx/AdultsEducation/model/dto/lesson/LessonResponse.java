package com.wairixx.AdultsEducation.model.dto.lesson;

public record LessonResponse(
        Long id, String title, String content, String videoUrl,
        Integer orderNumber, Long courseId) {}
