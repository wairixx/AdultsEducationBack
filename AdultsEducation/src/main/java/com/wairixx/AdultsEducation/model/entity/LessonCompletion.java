package com.wairixx.AdultsEducation.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lesson_completions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"education_id", "lesson_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LessonCompletion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "education_id", nullable = false)
    private Education education;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
}