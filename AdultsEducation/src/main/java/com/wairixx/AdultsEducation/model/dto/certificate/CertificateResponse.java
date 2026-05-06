package com.wairixx.AdultsEducation.model.dto.certificate;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CertificateResponse(
        Long id,
        String certificateNumber,
        LocalDate issueDate,
        String studentName,
        String courseTitle,
        String teacherName,
        Integer durationHours,
        Long educationId,
        Long courseId,
        LocalDateTime createdAt) {}