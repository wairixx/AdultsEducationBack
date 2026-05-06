package com.wairixx.AdultsEducation.repository;

import com.wairixx.AdultsEducation.model.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {
    List<Lesson> findByCourseIdOrderByOrderNumberAsc(Long courseId);
    boolean existsByCourseIdAndOrderNumber(Long courseId, Integer orderNumber);
    long countByCourseId(Long courseId);
}