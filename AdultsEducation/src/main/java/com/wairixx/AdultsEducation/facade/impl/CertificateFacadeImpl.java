package com.wairixx.AdultsEducation.facade.impl;

import com.wairixx.AdultsEducation.facade.CertificateFacade;
import com.wairixx.AdultsEducation.model.dto.certificate.CertificateResponse;
import com.wairixx.AdultsEducation.service.CertificateService;
import com.wairixx.AdultsEducation.util.CertificateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class CertificateFacadeImpl implements CertificateFacade {

    private final CertificateService certificateService;
    private final CertificateMapper certificateMapper;

    @Override
    @Transactional(readOnly = true)
    public CertificateResponse getByNumber(String number) {
        return certificateMapper.toResponse(certificateService.getByNumber(number));
    }

    @Override
    @Transactional(readOnly = true)
    public CertificateResponse getMyByEducation(Long educationId) {
        return certificateMapper.toResponse(certificateService.getMyCertificateForEducation(educationId));
    }

    @Override
    @Transactional(readOnly = true)
    public String renderHtml(String number) {
        return certificateService.renderAsHtml(certificateService.getByNumber(number));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] renderPdf(String number) {
        return certificateService.renderAsPdf(certificateService.getByNumber(number));
    }
}