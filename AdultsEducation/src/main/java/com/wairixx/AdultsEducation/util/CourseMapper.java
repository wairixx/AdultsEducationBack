package com.wairixx.AdultsEducation.util;

import com.wairixx.AdultsEducation.model.dto.course.CourseResponse;
import com.wairixx.AdultsEducation.model.entity.Course;
import com.wairixx.AdultsEducation.repository.EducationRepository;
import com.wairixx.AdultsEducation.repository.LessonRepository;
import com.wairixx.AdultsEducation.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseMapper {

    private final LessonRepository lessonRepository;
    private final EducationRepository educationRepository;
    private final ReviewRepository reviewRepository;

    public CourseResponse toResponse(Course c) {
        long lessons = lessonRepository.countByCourseId(c.getId());
        long students = educationRepository.count(
                (root, q, cb) -> cb.equal(root.get("course").get("id"), c.getId()));

        var reviews = reviewRepository.findAll(
                (root, q, cb) -> cb.and(
                        cb.equal(root.get("course").get("id"), c.getId()),
                        cb.isTrue(root.get("visible"))));
        double avg = reviews.stream().mapToInt(r -> r.getRating()).average().orElse(0.0);

        return new CourseResponse(
                c.getId(), c.getTitle(), c.getDescription(),
                c.getTopic(), c.getFormat(),
                c.getPrice(), c.getDurationHours(),
                c.getTeacher().getId(),
                NameUtils.fullName(c.getTeacher().getLastName(), c.getTeacher().getFirstName()),
                c.getTeacher().getAvatarUrl(),
                (int) lessons, (int) students,
                Math.round(avg * 10.0) / 10.0, reviews.size(),
                c.getVisible(),
                c.getCreatedAt(), c.getUpdatedAt(),
                c.getCoverUrl());
    }
}