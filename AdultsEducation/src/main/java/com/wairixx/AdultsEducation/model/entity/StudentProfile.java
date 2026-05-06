package com.wairixx.AdultsEducation.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "student_profiles")
@Getter @Setter @NoArgsConstructor
public class StudentProfile extends AbstractProfile {
}