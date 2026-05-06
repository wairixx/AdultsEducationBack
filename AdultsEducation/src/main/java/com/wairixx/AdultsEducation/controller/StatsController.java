package com.wairixx.AdultsEducation.controller;

import com.wairixx.AdultsEducation.model.enums.Role;
import com.wairixx.AdultsEducation.repository.CertificateRepository;
import com.wairixx.AdultsEducation.repository.CourseRepository;
import com.wairixx.AdultsEducation.repository.EducationRepository;
import com.wairixx.AdultsEducation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final CertificateRepository certificateRepository;

    @GetMapping("/public")
    public Map<String, Long> publicStats() {
        return Map.of(
                "coursesCount", courseRepository.count(),
                "studentsCount", userRepository.findAll().stream()
                        .filter(u -> u.getRole() == Role.STUDENT).count(),
                "teachersCount", userRepository.findAll().stream()
                        .filter(u -> u.getRole() == Role.TEACHER).count(),
                "certificatesIssued", certificateRepository.count()
        );
    }
}