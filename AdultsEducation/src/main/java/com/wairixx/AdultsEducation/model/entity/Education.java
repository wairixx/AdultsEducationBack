package com.wairixx.AdultsEducation.model.entity;

import com.wairixx.AdultsEducation.model.enums.EducationLevel;
import com.wairixx.AdultsEducation.model.enums.EducationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "educations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Education extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EducationStatus status;

    @Column(name = "enrolled_date", nullable = false)
    private LocalDate enrolledDate;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private EducationLevel level;

    @Column(columnDefinition = "TEXT")
    private String note;

    /** Progress percentage 0-100 (set by student, for UX) */
    @Column(nullable = false)
    private Integer progress = 0;
}