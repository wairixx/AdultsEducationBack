package com.wairixx.AdultsEducation.repository;

import com.wairixx.AdultsEducation.model.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {}