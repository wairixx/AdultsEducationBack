package com.wairixx.AdultsEducation.model.dto.course;
import com.wairixx.AdultsEducation.model.enums.CourseFormat;
import com.wairixx.AdultsEducation.model.enums.CourseTopic;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record CourseRequest(
        @NotBlank(message = "{error.course.title.blank}")
        @Size(max = 200, message = "{error.course.title.size}") String title,
        @NotBlank(message = "{error.course.description.blank}")
        @Size(max = 5000, message = "{error.course.description.size}") String description,
        @NotNull(message = "{error.course.topic.required}") CourseTopic topic,
        @NotNull(message = "{error.course.format.required}") CourseFormat format,
        @NotNull(message = "{error.course.price.required}")
        @DecimalMin(value = "0.0", message = "{error.course.price.min}") BigDecimal price,
        @NotNull(message = "{error.course.hours.required}")
        @Min(value = 1, message = "{error.course.hours.min}") Integer durationHours,
        @Size(max = 500) String coverUrl,
        Long teacherId
) {}