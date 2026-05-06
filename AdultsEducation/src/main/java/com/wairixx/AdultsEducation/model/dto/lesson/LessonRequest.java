package com.wairixx.AdultsEducation.model.dto.lesson;
import jakarta.validation.constraints.*;
public record LessonRequest(
        @NotBlank(message = "{error.lesson.title.blank}")
        @Size(max = 200, message = "{error.lesson.title.size}") String title,
        @Size(max = 20000) String content,
        @Size(max = 500) String videoUrl,
        @NotNull(message = "{error.lesson.order.required}")
        @Min(value = 1, message = "{error.lesson.order.min}") Integer orderNumber,
        @NotNull(message = "{error.lesson.course.required}") Long courseId) {}