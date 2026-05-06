package com.wairixx.AdultsEducation.model.entity;

import com.wairixx.AdultsEducation.model.enums.CourseFormat;
import com.wairixx.AdultsEducation.model.enums.CourseTopic;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Course extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CourseTopic topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CourseFormat format;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** Duration in hours */
    @Column(name = "duration_hours", nullable = false)
    private Integer durationHours;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private TeacherProfile teacher;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    /** Soft-hide for moderation */
    @Column(nullable = false)
    private Boolean visible = true;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Lesson> lessons = new ArrayList<>();
}