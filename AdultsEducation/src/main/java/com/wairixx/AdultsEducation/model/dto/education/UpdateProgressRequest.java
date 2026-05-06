package com.wairixx.AdultsEducation.model.dto.education;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
public record UpdateProgressRequest(
        @NotNull
        @Min(value = 0, message = "{error.education.progress.min}")
        @Max(value = 100, message = "{error.education.progress.max}") Integer progress) {}