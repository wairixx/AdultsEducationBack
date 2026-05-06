package com.wairixx.AdultsEducation.repository;

import com.wairixx.AdultsEducation.model.entity.Education;
import com.wairixx.AdultsEducation.model.enums.EducationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long>, JpaSpecificationExecutor<Education> {
    Optional<Education> findByStudentIdAndCourseId(Long studentId, Long courseId);
    boolean existsByStudentIdAndCourseIdAndStatusIn(Long studentId, Long courseId, java.util.Collection<EducationStatus> statuses);
    long countByCourseTeacherId(Long teacherId);
}