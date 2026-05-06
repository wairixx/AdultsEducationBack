package com.wairixx.AdultsEducation.controller;

import com.wairixx.AdultsEducation.model.dto.common.FileUploadResponse;
import com.wairixx.AdultsEducation.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Files", description = "File upload endpoints")
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/avatars", consumes = "multipart/form-data")
    @Operation(summary = "Upload avatar image (any authenticated user)")
    public ResponseEntity<FileUploadResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(new FileUploadResponse(
                fileStorageService.store(file, "avatars")));
    }

    @PostMapping(value = "/courses", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @Operation(summary = "Upload course cover image")
    public ResponseEntity<FileUploadResponse> uploadCourse(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(new FileUploadResponse(
                fileStorageService.store(file, "courses")));
    }
}