package com.wairixx.AdultsEducation.repository;

import com.wairixx.AdultsEducation.model.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    boolean existsByTitleAndTeacherId(String title, Long teacherId);
    long countByTeacherId(Long teacherId);
}