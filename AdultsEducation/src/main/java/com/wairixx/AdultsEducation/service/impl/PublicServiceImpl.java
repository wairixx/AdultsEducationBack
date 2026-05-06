package com.wairixx.AdultsEducation.service.impl;

import com.wairixx.AdultsEducation.aspect.Loggable;
import com.wairixx.AdultsEducation.exception.ResourceNotFoundException;
import com.wairixx.AdultsEducation.model.entity.User;
import com.wairixx.AdultsEducation.model.enums.Role;
import com.wairixx.AdultsEducation.repository.UserRepository;
import com.wairixx.AdultsEducation.service.PublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Loggable
public class PublicServiceImpl implements PublicService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User getTeacherById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.teacher.not.found"));

        if (user.getRole() != Role.TEACHER) {
            throw new ResourceNotFoundException("error.teacher.not.found");
        }

        return user;
    }
}