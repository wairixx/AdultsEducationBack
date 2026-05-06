package com.wairixx.AdultsEducation.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.wairixx.AdultsEducation.aspect.Loggable;
import com.wairixx.AdultsEducation.exception.BusinessException;
import com.wairixx.AdultsEducation.exception.ResourceNotFoundException;
import com.wairixx.AdultsEducation.model.entity.Certificate;
import com.wairixx.AdultsEducation.model.entity.Education;
import com.wairixx.AdultsEducation.repository.CertificateRepository;
import com.wairixx.AdultsEducation.service.CertificateService;
import com.wairixx.AdultsEducation.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Loggable
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;

    @Override
    @Transactional
    public Certificate issueCertificate(Education e) {
        // idempotent
        return certificateRepository.findByEducationId(e.getId()).orElseGet(() -> {
            Certificate c = new Certificate();
            c.setEducation(e);
            c.setCertificateNumber("CERT-" + UUID.randomUUID().toString()
                    .replace("-", "").substring(0, 12).toUpperCase());
            c.setIssueDate(e.getIssueDate() == null ? LocalDate.now() : e.getIssueDate());
            c.setStudentName(e.getStudent().getLastName() + " " + e.getStudent().getFirstName());
            c.setCourseTitle(e.getCourse().getTitle());
            c.setTeacherName(e.getCourse().getTeacher().getLastName() + " "
                    + e.getCourse().getTeacher().getFirstName());
            c.setDurationHours(e.getCourse().getDurationHours());
            return certificateRepository.save(c);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Certificate getByNumber(String number) {
        return certificateRepository.findByCertificateNumber(number)
                .orElseThrow(() -> new ResourceNotFoundException("error.certificate.not.found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Certificate getMyCertificateForEducation(Long educationId) {
        Certificate c = certificateRepository.findByEducationId(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("error.certificate.not.found"));
        Long me = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException("error.auth.required"));
        if (!SecurityUtils.isAdmin()
                && !c.getEducation().getStudent().getId().equals(me)
                && !c.getEducation().getCourse().getTeacher().getId().equals(me)) {
            throw new BusinessException("error.auth.access.denied");
        }
        return c;
    }

    @Override
    public String renderAsHtml(Certificate c) {
        return CertificateTemplate.render(c);
    }

    @Override
    public byte[] renderAsPdf(Certificate c) {
        String html = renderAsHtml(c);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            builder.useFont(() -> getClass().getResourceAsStream("/fonts/DejaVuSans.ttf"),
                    "DejaVu Sans", 400, PdfRendererBuilder.FontStyle.NORMAL, true);
            builder.useFont(() -> getClass().getResourceAsStream("/fonts/DejaVuSans-Bold.ttf"),
                    "DejaVu Sans", 700, PdfRendererBuilder.FontStyle.NORMAL, true);
            builder.useFont(() -> getClass().getResourceAsStream("/fonts/PlayfairDisplay-Bold.ttf"),
                    "Playfair Display", 700, PdfRendererBuilder.FontStyle.NORMAL, true);
            builder.useFont(() -> getClass().getResourceAsStream("/fonts/PlayfairDisplay-BoldItalic.ttf"),
                    "Playfair Display", 700, PdfRendererBuilder.FontStyle.ITALIC, true);

            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception ex) {
            throw new BusinessException("error.certificate.pdf.failed");
        }
    }
}