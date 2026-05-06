package com.wairixx.AdultsEducation.service;

import com.wairixx.AdultsEducation.model.dto.review.ReviewFilter;
import com.wairixx.AdultsEducation.model.dto.review.ReviewRequest;
import com.wairixx.AdultsEducation.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    Review create(ReviewRequest request);
    Review getById(Long id);
    Page<Review> getAll(ReviewFilter filter, Pageable pageable, boolean onlyVisible);
    void delete(Long id);
    Review setVisibility(Long id, boolean visible);
}