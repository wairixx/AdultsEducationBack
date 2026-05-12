package com.wairixx.AdultsEducation.model.dto.course;
import com.wairixx.AdultsEducation.model.enums.CourseFormat;
import com.wairixx.AdultsEducation.model.enums.CourseTopic;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record CourseResponse(
        Long id, String title, String description,
        CourseTopic topic, CourseFormat format,
        BigDecimal price, Integer durationHours,
        Long teacherId, String teacherFullName, String teacherAvatarUrl,
        Integer lessonsCount, Integer studentsCount,
        Double averageRating, Integer reviewsCount,
        Boolean visible,
        LocalDateTime createdAt, LocalDateTime updatedAt,
        String coverUrl) {}