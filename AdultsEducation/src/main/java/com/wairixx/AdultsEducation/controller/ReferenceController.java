package com.wairixx.AdultsEducation.controller;

import com.wairixx.AdultsEducation.model.enums.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/reference")
@Tag(name = "Reference", description = "Enum dictionaries for frontend")
public class ReferenceController {

    @GetMapping("/course-topics")
    public List<String> topics() {
        return Arrays.stream(CourseTopic.values()).map(Enum::name).toList();
    }

    @GetMapping("/course-formats")
    public List<String> formats() {
        return Arrays.stream(CourseFormat.values()).map(Enum::name).toList();
    }

    @GetMapping("/payment-methods")
    public List<String> methods() {
        return Arrays.stream(PaymentMethod.values()).map(Enum::name).toList();
    }

    @GetMapping("/education-statuses")
    public List<String> statuses() {
        return Arrays.stream(EducationStatus.values()).map(Enum::name).toList();
    }

    @GetMapping("/education-levels")
    public List<String> levels() {
        return Arrays.stream(EducationLevel.values()).map(Enum::name).toList();
    }
}