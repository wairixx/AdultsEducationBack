package com.wairixx.AdultsEducation.facade.impl;

import com.wairixx.AdultsEducation.facade.MeFacade;
import com.wairixx.AdultsEducation.model.dto.user.ChangePasswordRequest;
import com.wairixx.AdultsEducation.model.dto.user.UpdateMeRequest;
import com.wairixx.AdultsEducation.model.dto.user.UserResponse;
import com.wairixx.AdultsEducation.service.MeService;
import com.wairixx.AdultsEducation.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeFacadeImpl implements MeFacade {

    private final MeService meService;
    private final UserMapper userMapper;

    @Override public UserResponse getMyProfile() { return userMapper.toResponse(meService.getMyProfile()); }

    @Override
    public UserResponse updateMyProfile(UpdateMeRequest request) {
        return userMapper.toResponse(meService.updateMyProfile(request));
    }

    @Override public void changeMyPassword(ChangePasswordRequest request) { meService.changeMyPassword(request); }
}