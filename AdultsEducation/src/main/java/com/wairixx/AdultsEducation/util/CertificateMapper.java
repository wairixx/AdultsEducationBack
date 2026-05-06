package com.wairixx.AdultsEducation.util;

import com.wairixx.AdultsEducation.model.dto.certificate.CertificateResponse;
import com.wairixx.AdultsEducation.model.entity.Certificate;
import org.springframework.stereotype.Component;

@Component
public class CertificateMapper {
    public CertificateResponse toResponse(Certificate c) {
        return new CertificateResponse(
                c.getId(), c.getCertificateNumber(), c.getIssueDate(),
                c.getStudentName(), c.getCourseTitle(), c.getTeacherName(),
                c.getDurationHours(),
                c.getEducation().getId(), c.getEducation().getCourse().getId(),
                c.getCreatedAt());
    }
}