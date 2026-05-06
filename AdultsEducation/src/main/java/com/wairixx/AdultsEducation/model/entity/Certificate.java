package com.wairixx.AdultsEducation.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "certificates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Certificate extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "education_id", nullable = false, unique = true)
    private Education education;

    /** Public unique code for share link (UUID). */
    @Column(name = "certificate_number", nullable = false, unique = true, length = 50)
    private String certificateNumber;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    /** Cached student full name snapshot (in case profile edits later). */
    @Column(name = "student_name", nullable = false, length = 200)
    private String studentName;

    @Column(name = "course_title", nullable = false, length = 300)
    private String courseTitle;

    @Column(name = "teacher_name", nullable = false, length = 200)
    private String teacherName;

    @Column(name = "duration_hours", nullable = false)
    private Integer durationHours;
}