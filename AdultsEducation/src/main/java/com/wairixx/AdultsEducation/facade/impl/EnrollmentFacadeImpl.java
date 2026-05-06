package com.wairixx.AdultsEducation.facade.impl;

import com.wairixx.AdultsEducation.facade.EnrollmentFacade;
import com.wairixx.AdultsEducation.model.dto.education.EnrollmentRequest;
import com.wairixx.AdultsEducation.model.dto.education.EnrollmentResponse;
import com.wairixx.AdultsEducation.service.EnrollmentService;
import com.wairixx.AdultsEducation.util.EducationMapper;
import com.wairixx.AdultsEducation.util.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrollmentFacadeImpl implements EnrollmentFacade {

    private final EnrollmentService enrollmentService;
    private final EducationMapper educationMapper;
    private final PaymentMapper paymentMapper;

    @Override
    public EnrollmentResponse enroll(EnrollmentRequest request) {
        var r = enrollmentService.enroll(request);
        return new EnrollmentResponse(
                educationMapper.toResponse(r.education()),
                paymentMapper.toResponse(r.payment()));
    }
}