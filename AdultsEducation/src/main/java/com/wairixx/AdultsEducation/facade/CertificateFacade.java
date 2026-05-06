package com.wairixx.AdultsEducation.facade;

import com.wairixx.AdultsEducation.model.dto.certificate.CertificateResponse;

public interface CertificateFacade {
    CertificateResponse getByNumber(String number);
    CertificateResponse getMyByEducation(Long educationId);
    String renderHtml(String number);
    byte[] renderPdf(String number);
}