package com.wairixx.AdultsEducation.model.dto.education;
import com.wairixx.AdultsEducation.model.dto.payment.PaymentResponse;
public record EnrollmentResponse(EducationResponse education, PaymentResponse payment) {}
