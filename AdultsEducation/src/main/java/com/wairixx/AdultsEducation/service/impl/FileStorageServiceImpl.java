package com.wairixx.AdultsEducation.service.impl;

import com.wairixx.AdultsEducation.aspect.Loggable;
import com.wairixx.AdultsEducation.config.UploadProperties;
import com.wairixx.AdultsEducation.exception.BusinessException;
import com.wairixx.AdultsEducation.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Loggable(logArgs = false)
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final UploadProperties props;

    @Override
    public String store(MultipartFile file, String subDirectory) {
        validate(file);
        try {
            Path dir = Paths.get(props.getDirectory(), subDirectory).toAbsolutePath().normalize();
            Files.createDirectories(dir);

            String ext = extractExtension(file.getContentType());
            String filename = UUID.randomUUID() + ext;
            Path target = dir.resolve(filename);

            file.transferTo(target);

            String url = props.getBaseUrl() + "/" + subDirectory + "/" + filename;
            log.info("File stored: {}", url);
            return url;
        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new BusinessException("error.file.upload.failed");
        }
    }

    @Override
    public void delete(String url) {
        if (url == null || !url.startsWith(props.getBaseUrl())) return;
        try {
            String relative = url.substring(props.getBaseUrl().length());
            Path file = Paths.get(props.getDirectory(), relative).toAbsolutePath().normalize();
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warn("Failed to delete file {}: {}", url, e.getMessage());
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new BusinessException("error.file.empty");
        if (file.getSize() > props.getMaxSizeBytes())
            throw new BusinessException("error.file.too.large");
        String ct = file.getContentType();
        if (ct == null || !props.getAllowedContentTypes().contains(ct))
            throw new BusinessException("error.file.type.not.allowed");
    }

    private String extractExtension(String ct) {
        return switch (ct) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }
}