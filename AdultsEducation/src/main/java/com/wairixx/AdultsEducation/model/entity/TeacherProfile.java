package com.wairixx.AdultsEducation.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teacher_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TeacherProfile extends AbstractProfile {

    @Column(length = 100)
    private String specialization;

    @Column(name = "experience_years")
    private Double experienceYears;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private List<Course> courses = new ArrayList<>();
}