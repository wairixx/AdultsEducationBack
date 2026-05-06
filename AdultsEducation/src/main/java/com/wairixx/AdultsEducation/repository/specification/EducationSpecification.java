package com.wairixx.AdultsEducation.repository.specification;

import com.wairixx.AdultsEducation.model.dto.education.EducationFilter;
import com.wairixx.AdultsEducation.model.entity.Education;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static com.wairixx.AdultsEducation.repository.specification.SpecificationUtils.*;

public final class EducationSpecification {

    private EducationSpecification() {}

    public static Specification<Education> withFilter(EducationFilter f) {
        if (f == null) return alwaysTrue();
        List<Specification<Education>> specs = new ArrayList<>();
        if (f.studentId() != null)
            specs.add((r, q, cb) -> cb.equal(r.get("student").get("id"), f.studentId()));
        if (f.courseId() != null)
            specs.add((r, q, cb) -> cb.equal(r.get("course").get("id"), f.courseId()));
        if (f.teacherId() != null)
            specs.add((r, q, cb) -> cb.equal(r.get("course").get("teacher").get("id"), f.teacherId()));
        if (f.status() != null)
            specs.add((r, q, cb) -> cb.equal(r.get("status"), f.status()));
        return specs.isEmpty() ? alwaysTrue() : Specification.allOf(specs);
    }
}