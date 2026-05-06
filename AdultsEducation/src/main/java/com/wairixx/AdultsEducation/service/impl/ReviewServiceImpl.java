package com.wairixx.AdultsEducation.service.impl;

import com.wairixx.AdultsEducation.aspect.Loggable;
import com.wairixx.AdultsEducation.exception.BusinessException;
import com.wairixx.AdultsEducation.exception.DuplicateResourceException;
import com.wairixx.AdultsEducation.exception.ResourceNotFoundException;
import com.wairixx.AdultsEducation.model.dto.review.ReviewFilter;
import com.wairixx.AdultsEducation.model.dto.review.ReviewRequest;
import com.wairixx.AdultsEducation.model.entity.Course;
import com.wairixx.AdultsEducation.model.entity.Review;
import com.wairixx.AdultsEducation.model.entity.StudentProfile;
import com.wairixx.AdultsEducation.model.entity.User;
import com.wairixx.AdultsEducation.model.enums.EducationStatus;
import com.wairixx.AdultsEducation.model.enums.Role;
import com.wairixx.AdultsEducation.repository.CourseRepository;
import com.wairixx.AdultsEducation.repository.EducationRepository;
import com.wairixx.AdultsEducation.repository.ReviewRepository;
import com.wairixx.AdultsEducation.repository.StudentProfileRepository;
import com.wairixx.AdultsEducation.repository.specification.ReviewSpecification;
import com.wairixx.AdultsEducation.service.ReviewService;
import com.wairixx.AdultsEducation.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Loggable
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final EducationRepository educationRepository;
    private final StudentProfileRepository studentProfileRepository;

    @Override
    @Transactional
    public Review create(ReviewRequest r) {

        User currentUser = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new BusinessException("error.auth.required"));

        StudentProfile student;
        boolean isAdminOverride = currentUser.getRole() == Role.ADMIN && r.studentId() != null;

        if (isAdminOverride) {
            // Admin creates review on behalf of a specific student
            student = studentProfileRepository.findById(r.studentId())
                    .orElseThrow(() -> new BusinessException("error.enrollment.student.profile.missing"));
        } else {
            if (currentUser.getRole() != Role.STUDENT)
                throw new BusinessException("error.enrollment.only.students");
            student = studentProfileRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new BusinessException("error.enrollment.student.profile.missing"));
        }

        Course course = courseRepository.findById(r.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("error.course.not.found"));

        // Залишити відгук можна тільки після проходження курсу (skip for admin override)
        if (!isAdminOverride) {
            boolean enrolled = educationRepository.existsByStudentIdAndCourseIdAndStatusIn(
                    student.getId(), course.getId(),
                    List.of(EducationStatus.ACTIVE, EducationStatus.COMPLETED));
            if (!enrolled)
                throw new BusinessException("error.review.not.enrolled");
        }

        if (reviewRepository.existsByStudentIdAndCourseId(student.getId(), course.getId()))
            throw new DuplicateResourceException("error.review.duplicate");

        Review review = new Review();
        review.setStudent(student);
        review.setCourse(course);
        review.setRating(r.rating());
        review.setComment(r.comment());
        review.setVisible(true);
        return reviewRepository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Review getById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.review.not.found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Review> getAll(ReviewFilter filter, Pageable pageable, boolean onlyVisible) {
        return reviewRepository.findAll(ReviewSpecification.withFilter(filter, onlyVisible), pageable);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Review r = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.review.not.found"));
        ensureCanModify(r);
        reviewRepository.delete(r);
    }

    @Override
    @Transactional
    public Review setVisibility(Long id, boolean visible) {
        if (!SecurityUtils.isAdmin())
            throw new BusinessException("error.auth.access.denied");
        Review r = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.review.not.found"));
        r.setVisible(visible);
        return r;
    }

    private void ensureCanModify(Review r) {
        if (SecurityUtils.isAdmin()) return;
        Long me = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException("error.auth.required"));
        if (!r.getStudent().getId().equals(me))
            throw new BusinessException("error.auth.access.denied");
    }
}