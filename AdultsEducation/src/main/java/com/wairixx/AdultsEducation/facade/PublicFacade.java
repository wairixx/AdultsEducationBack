package com.wairixx.AdultsEducation.facade;

import com.wairixx.AdultsEducation.model.dto.user.UserResponse;

public interface PublicFacade {
    UserResponse getTeacherById(Long id);
}