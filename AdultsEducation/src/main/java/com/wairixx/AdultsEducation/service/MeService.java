package com.wairixx.AdultsEducation.service;

import com.wairixx.AdultsEducation.model.dto.user.ChangePasswordRequest;
import com.wairixx.AdultsEducation.model.dto.user.UpdateMeRequest;
import com.wairixx.AdultsEducation.model.entity.User;

public interface MeService {
    User getMyProfile();
    User updateMyProfile(UpdateMeRequest request);
    void changeMyPassword(ChangePasswordRequest request);
}