package com.wairixx.AdultsEducation.model.dto.education;
import com.wairixx.AdultsEducation.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record EnrollmentRequest(
        @NotNull(message = "{error.enrollment.course.required}") Long courseId,
        @NotNull(message = "{error.enrollment.method.required}") PaymentMethod paymentMethod,
        @Size(max = 100) String transactionRef,
        Long studentId) {}