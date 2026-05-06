package com.wairixx.AdultsEducation.repository.specification;

import com.wairixx.AdultsEducation.model.dto.course.CourseFilter;
import com.wairixx.AdultsEducation.model.entity.Course;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static com.wairixx.AdultsEducation.repository.specification.SpecificationUtils.*;

public final class CourseSpecification {

    private CourseSpecification() {}

    public static Specification<Course> withFilter(CourseFilter f, boolean onlyVisible) {
        List<Specification<Course>> specs = new ArrayList<>();
        if (f != null) {
            addIfNotNull(specs, likeIgnoreCase("title", f.title()));
            if (f.topic() != null)
                specs.add((r, q, cb) -> cb.equal(r.get("topic"), f.topic()));
            if (f.format() != null)
                specs.add((r, q, cb) -> cb.equal(r.get("format"), f.format()));
            addIfNotNull(specs, rangeSpec("price", f.minPrice(), f.maxPrice()));
            addIfNotNull(specs, rangeSpec("durationHours", f.minHours(), f.maxHours()));
            if (f.teacherId() != null)
                specs.add((r, q, cb) -> cb.equal(r.get("teacher").get("id"), f.teacherId()));
        }
        if (onlyVisible) {
            specs.add((r, q, cb) -> cb.isTrue(r.get("visible")));
        }
        return specs.isEmpty() ? alwaysTrue() : Specification.allOf(specs);
    }
}