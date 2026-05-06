package com.wairixx.AdultsEducation.facade;

import com.wairixx.AdultsEducation.model.dto.education.EnrollmentRequest;
import com.wairixx.AdultsEducation.model.dto.education.EnrollmentResponse;

public interface EnrollmentFacade {
    EnrollmentResponse enroll(EnrollmentRequest request);
}