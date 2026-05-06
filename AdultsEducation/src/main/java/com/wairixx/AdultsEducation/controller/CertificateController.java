package com.wairixx.AdultsEducation.controller;

import com.wairixx.AdultsEducation.facade.CertificateFacade;
import com.wairixx.AdultsEducation.model.dto.certificate.CertificateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
@Tag(name = "Certificates", description = "Course completion certificates")
public class CertificateController {

    private final CertificateFacade certificateFacade;

    /** Публічна верифікація за номером (гості теж можуть перевірити). */
    @GetMapping("/verify/{number}")
    @Operation(summary = "Verify certificate by public number")
    public ResponseEntity<CertificateResponse> verify(@PathVariable String number) {
        return ResponseEntity.ok(certificateFacade.getByNumber(number));
    }

    /** HTML для перегляду в браузері / друку. */
    @GetMapping(value = "/verify/{number}/view", produces = "text/html; charset=UTF-8")
    @Operation(summary = "View certificate as HTML")
    public ResponseEntity<String> viewHtml(@PathVariable String number) {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/html; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(certificateFacade.renderHtml(number));
    }

    /** PDF для скачування (опційно). */
    @GetMapping(value = "/verify/{number}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Download certificate as PDF")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String number) {
        byte[] data = certificateFacade.renderPdf(number);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"certificate-" + number + ".pdf\"")
                .body(data);
    }

    /** Мій сертифікат по конкретному навчанню. */
    @GetMapping("/by-education/{educationId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my certificate for a specific enrollment")
    public ResponseEntity<CertificateResponse> getMyByEducation(@PathVariable Long educationId) {
        return ResponseEntity.ok(certificateFacade.getMyByEducation(educationId));
    }
}