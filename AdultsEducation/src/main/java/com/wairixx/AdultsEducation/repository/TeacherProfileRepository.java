package com.wairixx.AdultsEducation.repository;

import com.wairixx.AdultsEducation.model.entity.TeacherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long>,
        JpaSpecificationExecutor<TeacherProfile> {}