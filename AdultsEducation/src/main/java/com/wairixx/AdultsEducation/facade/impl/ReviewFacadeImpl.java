package com.wairixx.AdultsEducation.facade.impl;

import com.wairixx.AdultsEducation.facade.ReviewFacade;
import com.wairixx.AdultsEducation.model.dto.review.ReviewFilter;
import com.wairixx.AdultsEducation.model.dto.review.ReviewRequest;
import com.wairixx.AdultsEducation.model.dto.review.ReviewResponse;
import com.wairixx.AdultsEducation.service.ReviewService;
import com.wairixx.AdultsEducation.util.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReviewFacadeImpl implements ReviewFacade {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewResponse create(ReviewRequest request) {
        return reviewMapper.toResponse(reviewService.create(request));
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getById(Long id) {
        return reviewMapper.toResponse(reviewService.getById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getAllPublic(ReviewFilter filter, Pageable pageable) {
        return reviewService.getAll(filter, pageable, true).map(reviewMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getAllForAdmin(ReviewFilter filter, Pageable pageable) {
        return reviewService.getAll(filter, pageable, false).map(reviewMapper::toResponse);
    }

    @Override public void delete(Long id) { reviewService.delete(id); }

    @Override
    @Transactional
    public ReviewResponse setVisibility(Long id, boolean visible) {
        return reviewMapper.toResponse(reviewService.setVisibility(id, visible));
    }
}