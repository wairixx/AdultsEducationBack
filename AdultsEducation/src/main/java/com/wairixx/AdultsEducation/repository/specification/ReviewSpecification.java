package com.wairixx.AdultsEducation.repository.specification;

import com.wairixx.AdultsEducation.model.dto.review.ReviewFilter;
import com.wairixx.AdultsEducation.model.entity.Review;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static com.wairixx.AdultsEducation.repository.specification.SpecificationUtils.*;

public final class ReviewSpecification {

    private ReviewSpecification() {}

    public static Specification<Review> withFilter(ReviewFilter f, boolean onlyVisible) {
        List<Specification<Review>> specs = new ArrayList<>();
        if (f != null) {
            if (f.courseId() != null)
                specs.add((r, q, cb) -> cb.equal(r.get("course").get("id"), f.courseId()));
            if (f.studentId() != null)
                specs.add((r, q, cb) -> cb.equal(r.get("student").get("id"), f.studentId()));
            addIfNotNull(specs, rangeSpec("rating", f.minRating(), f.maxRating()));
        }
        if (onlyVisible) specs.add((r, q, cb) -> cb.isTrue(r.get("visible")));
        return specs.isEmpty() ? alwaysTrue() : Specification.allOf(specs);
    }
}