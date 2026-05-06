package com.wairixx.AdultsEducation.facade;

import com.wairixx.AdultsEducation.model.dto.review.ReviewFilter;
import com.wairixx.AdultsEducation.model.dto.review.ReviewRequest;
import com.wairixx.AdultsEducation.model.dto.review.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewFacade {
    ReviewResponse create(ReviewRequest request);
    ReviewResponse getById(Long id);
    Page<ReviewResponse> getAllPublic(ReviewFilter filter, Pageable pageable);
    Page<ReviewResponse> getAllForAdmin(ReviewFilter filter, Pageable pageable);
    void delete(Long id);
    ReviewResponse setVisibility(Long id, boolean visible);
}