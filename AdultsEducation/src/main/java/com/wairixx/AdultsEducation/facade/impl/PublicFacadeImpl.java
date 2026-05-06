package com.wairixx.AdultsEducation.facade.impl;

import com.wairixx.AdultsEducation.facade.PublicFacade;
import com.wairixx.AdultsEducation.model.dto.user.UserResponse;
import com.wairixx.AdultsEducation.service.PublicService;
import com.wairixx.AdultsEducation.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PublicFacadeImpl implements PublicFacade {

    private final PublicService publicService;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getTeacherById(Long id) {
        // Отримуємо сутність User (яка є вчителем) з сервісу і мапимо у UserResponse
        return userMapper.toResponse(publicService.getTeacherById(id));
    }
}