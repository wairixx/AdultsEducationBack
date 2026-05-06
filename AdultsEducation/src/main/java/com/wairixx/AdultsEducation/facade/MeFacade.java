package com.wairixx.AdultsEducation.facade;

import com.wairixx.AdultsEducation.model.dto.user.ChangePasswordRequest;
import com.wairixx.AdultsEducation.model.dto.user.UpdateMeRequest;
import com.wairixx.AdultsEducation.model.dto.user.UserResponse;

public interface MeFacade {
    UserResponse getMyProfile();
    UserResponse updateMyProfile(UpdateMeRequest request);
    void changeMyPassword(ChangePasswordRequest request);
}