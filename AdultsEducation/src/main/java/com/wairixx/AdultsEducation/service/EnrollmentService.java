package com.wairixx.AdultsEducation.service;

import com.wairixx.AdultsEducation.model.dto.education.EnrollmentRequest;
import com.wairixx.AdultsEducation.model.entity.Education;
import com.wairixx.AdultsEducation.model.entity.Payment;

public interface EnrollmentService {
    record EnrollmentResult(Education education, Payment payment) {}
    EnrollmentResult enroll(EnrollmentRequest request);
}
