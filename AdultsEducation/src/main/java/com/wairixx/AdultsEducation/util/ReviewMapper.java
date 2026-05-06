package com.wairixx.AdultsEducation.util;

import com.wairixx.AdultsEducation.model.dto.review.ReviewResponse;
import com.wairixx.AdultsEducation.model.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review r) {
        return new ReviewResponse(
                r.getId(), r.getCourse().getId(), r.getCourse().getTitle(),
                r.getStudent().getId(),
                NameUtils.fullName(r.getStudent().getLastName(), r.getStudent().getFirstName()),
                r.getRating(), r.getComment(), r.getVisible(), r.getCreatedAt());
    }
}