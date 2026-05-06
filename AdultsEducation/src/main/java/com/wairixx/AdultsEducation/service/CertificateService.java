package com.wairixx.AdultsEducation.service;

import com.wairixx.AdultsEducation.model.entity.Certificate;
import com.wairixx.AdultsEducation.model.entity.Education;

public interface CertificateService {
    Certificate issueCertificate(Education education);
    Certificate getByNumber(String number);
    Certificate getMyCertificateForEducation(Long educationId);
    String renderAsHtml(Certificate certificate);
    byte[] renderAsPdf(Certificate certificate);
}