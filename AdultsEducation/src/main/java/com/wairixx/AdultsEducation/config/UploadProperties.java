package com.wairixx.AdultsEducation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.uploads")
@Getter
@Setter
public class UploadProperties {
    private String directory;
    private String baseUrl;
    private long maxSizeBytes;
    private List<String> allowedContentTypes;
}