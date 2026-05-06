package com.wairixx.AdultsEducation.model.dto.course;
import com.wairixx.AdultsEducation.model.enums.CourseFormat;
import com.wairixx.AdultsEducation.model.enums.CourseTopic;
import java.math.BigDecimal;

public record CourseFilter(
        String title, CourseTopic topic, CourseFormat format,
        BigDecimal minPrice, BigDecimal maxPrice,
        Integer minHours, Integer maxHours,
        Long teacherId) {}