package com.wairixx.AdultsEducation.repository;

import com.wairixx.AdultsEducation.model.entity.LessonCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Long> {
    boolean existsByEducationIdAndLessonId(Long educationId, Long lessonId);
    long countByEducationId(Long educationId);
    List<LessonCompletion> findByEducationId(Long educationId);
    void deleteByEducationIdAndLessonId(Long educationId, Long lessonId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM LessonCompletion lc WHERE lc.lesson.id IN (SELECT l.id FROM Lesson l WHERE l.course.id = :courseId)")
    void deleteByLessonCourseId(@org.springframework.data.repository.query.Param("courseId") Long courseId);
}