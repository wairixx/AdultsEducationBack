package com.wairixx.AdultsEducation.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lessons",
        uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "order_number"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Lesson extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}